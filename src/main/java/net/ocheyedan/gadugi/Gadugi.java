package net.ocheyedan.gadugi;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Date: 3/2/12
 * Time: 4:17 PM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * To use this within your application include the {@literal gadugi} libary on the classpath and set the following
 * system property {@literal -Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi}.  For example:
 * <pre>
 *     java -Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi -cp YOUR_CLASSPATH YOUR_MAIN_CLASS
 * </pre>
 * <p/>
 * Within your application make calls to {@link #using(String)} to set a particular library's version
 * before calling the library code.  The {@linkplain String} value should be the property name in the configuration
 * file defined below.
 * <p/>
 * To define libraries and their versions include a {@literal proprties} file on the classpath.  The location of the file
 * on the classpath is controlled by the system property {@literal gadugi.config}, @see {@link Configuration} for more
 * information.
 * The format of the file should be:
 * <pre>
 * library_version_identifier=absolute path to library version's jar
 * </pre>
 * // TODO - specifying defaults
 * <p/>
 * Note, all libraries which can be used safely throughout the whole code base should be included on the classpath.  The
 * libraries with multiple versions must be <b>excluded</b> from the classpath and only referenced within the
 * {@literal proprties} file.  This allows {@link Gadugi} to only specify one {@link ClassLoader} per library version and
 * then delegate to a default classloader which includes all classpath entries.
 */
public final class Gadugi extends URLClassLoader {

    /**
     * Holds the library version which the user is currently using.
     */
    private static final ThreadLocal<String> using = new ThreadLocal<String>();

    /**
     * Sets the library version to use for the calling thread's subsequent invocations.
     * @param libraryVersion of the library to use on the calling thread.
     */
    public static void using(String libraryVersion) {
        using.set(libraryVersion);
    }
    
    /**
     * A mapping of library version identifier (that passed into {@link #using(String)}) to the associated
     * {@link LibraryVersionLoader}.
     */
    private final ConcurrentMap<String, LibraryVersionLoader> classLoaders = new ConcurrentHashMap<String, LibraryVersionLoader>();

    /**
     * Since {@link #init()} must happen lazily, this variable is used to track whether {@link #init()} has been
     * yet called.
     */
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * The normal process for resolving a {@link Class} is:
     * -1- check if there is a mapped {@link LibraryVersionLoader}
     * -1.a- if so, load using its {@link LibraryVersionLoader#_loadClass(String, boolean)}.
     * -1.b- otherwise load via {@link super#loadClass(String, boolean)}
     * The {@link LibraryVersionLoader} process is the same as its super, {@link URLClassLoader} which will first
     * check its {@link URLClassLoader#parent} which for {@link LibraryVersionLoader} objects is {@literal this}.
     * Checking {@literal this} is problematic as it causes infinite recursion.  To prevent this, each class name while
     * its class is being loaded is added into this {@link Map} as a key.  That way {@literal this} can skip the -1- check
     * and do -1.b- directly when being called as the parent of the {@link LibraryVersionLoader} object.
     * <p/>
     * Note the use of a {@link ConcurrentMap} where a {@link Set} is more apt.  This is to utilize the concurrency
     * niceties of {@link ConcurrentMap}.  The value for every key placed within this map will be the same; the empty
     * string {@literal ""}.
     */
    private final ConcurrentMap<String, String> gadugiEncountered = new ConcurrentHashMap<String, String>();

    /**
     * The 'user-land' {@link #using} {@linkplain Field} value. This is a cache of the reflection look-up.
     * @see #get()
     */
    private final AtomicReference<Field> usingField = new AtomicReference<Field>();

    public Gadugi(ClassLoader parent) {
        // parent will be sun.misc.Launcher$AppClassLoader - steal its URLs and its parent; in essence we're emulating it
        this(((URLClassLoader) parent).getURLs(), parent.getParent());
    }
    
    private Gadugi(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Converts the configuration given by the user into applicable {@link LibraryVersionLoader} objects and places them
     * within the {@link #classLoaders} map.
     * <p/>
     * Note, this must be called lazily as it attempts to load two classes {@link Configuration} and {@link LibraryVersionLoader}.
     * Classes cannot be loaded until after construction as this class is the system class loader.
     */
    private void init() {
        Map<String, String> config = Configuration.get();
        for (String libraryVersion : config.keySet()) {
            LibraryVersionLoader classLoader = create(libraryVersion, config.get(libraryVersion));
            if (classLoader != null) {
                classLoaders.put(libraryVersion, classLoader);
            }
        }
    }

    /**
     * @param libraryVersionId the name of the library version given by the user
     * @param libraryVersionLocation the location of the jar file associated with {@code libraryVersionIdentifier}
     * @return a {@link LibraryVersionLoader} whose parent is {@literal this} and which points to the jar file at
     *         {@code libraryVersionLocation} or null if {@code libraryVersionLocation} is malformed.
     */
    private LibraryVersionLoader create(String libraryVersionId, String libraryVersionLocation) {
        try {
            File libraryVersionFile = new File(libraryVersionLocation);
            return new LibraryVersionLoader(new URL[] { libraryVersionFile.toURI().toURL() }, this, libraryVersionId);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Invalid library.version [ %s ].", libraryVersionLocation), e);
        }
    }

    /**
     * Delegate to {@link #loadClass(String, boolean)} passing in {@literal false}
     * @param name of the {@linkplain Class} to load
     * @return the loaded {@linkplain Class} object named {@code name}
     * @throws ClassNotFoundException @see {@link #loadClass(String, boolean)}
     */
    @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    /**
     * The {@literal gadugi} process for class loading is as follows:
     * If the {@code name} starts with {@literal java}, load by calling {@link super#loadClass(String, boolean)},
     * if {@link #gadugiEncountered} contains {@code name} then load by calling {@link super#loadClass(String, boolean)}
     * if {@link #classLoaders} contains a value for the library version returned by {@link #get()} use the returned
     * {@link LibraryVersionLoader} to load the class, otherwise call {@link super#loadClass(String, boolean)}
     * 
     * @param name of the {@linkplain Class} to load
     * @param resolve @see {@link URLClassLoader#loadClass(String, boolean)}
     * @return the loaded {@linkplain Class} object with name {@code name}
     * @throws ClassNotFoundException if a {@linkplain Class} with name {@code name} cannot be found
     */
    @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // skip system libraries as they are used in loading {@link Gadugi} itself and would cause ClassCircularityError
        // exceptions if we attempt to call {@link #get()}, plus, we don't support loading of multiple versions of
        // the core java classes anyway
        if (name.startsWith("java")) {
            return super.loadClass(name, resolve);
        }

        try {
            if (gadugiEncountered.putIfAbsent(name, "") != null) {
                return super.loadClass(name, resolve);
            }
            if (!initialized.getAndSet(true)) {
                init();
            }
            String libraryVersion = get();
            LibraryVersionLoader loader = (libraryVersion == null ? null : classLoaders.get(libraryVersion));
            Class<?> clazz;
            if (loader != null) {
                clazz = loader._loadClass(name, resolve);
            } else {
                clazz = super.loadClass(name, resolve);
            }
            if (clazz.getClassLoader() == null) {
                System.out.format("^info^ loaded %s (with <boot> classloader) (using %s) on thread %s%n", name, libraryVersion,
                        Thread.currentThread().getName());
            } else {
                System.out.format("loaded %s (with %s classloader) (using %s) on thread %s%n", name, clazz.getClassLoader(),
                        libraryVersion, Thread.currentThread().getName());
            }
            return clazz;
        } finally {
            gadugiEncountered.remove(name);
        }
    }

    /**
     * @return the "user-land" value of the {@link #using} variable or null if {@link Gadugi} has not yet been
     *         loaded in the 'user-land' or is explicitly set as null.
     */
    @SuppressWarnings("unchecked")
    private String get() {
        // prevent ClassCircularityError...do not attempt to retrieve the value of {@link #using} until
        // the user's code has invoked the {@link #using(String)} method (i.e., user's Gadugi class has been loaded).
        Class<?> userGadugi = findLoadedClass("net.ocheyedan.gadugi.Gadugi");
        if (userGadugi == null) {
            return null;
        }
        try {
            if (usingField.get() == null) { // not synchronized; at worst this will be called more than once
                Field userUsingField = userGadugi.getDeclaredField("using");
                userUsingField.setAccessible(true);
                usingField.set(userUsingField);
            }
            return ((ThreadLocal<String>) usingField.get().get(null)).get();
        } catch (NoSuchFieldException nsfe) {
            throw new AssertionError(nsfe);
        } catch (IllegalAccessException iae) {
            throw new AssertionError(iae);
        }
    }

    @Override public String toString() {
        return "Gadugi!";
    }

}