package net.ocheyedan.gadugi;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

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
 * Within your application code make calls to {@link #using(LibraryVersion)} to set a particular library's version
 * before calling the library code.
 */
public final class Gadugi extends URLClassLoader {

    private static final ThreadLocal<LibraryVersion> using = new ThreadLocal<LibraryVersion>();

    /**
     * Sets the library version to use for the calling thread's subsequent invocations.
     * @param libraryVersion of the library to use on the calling thread.
     */
    public static void using(LibraryVersion libraryVersion) {
        using.set(libraryVersion);
    }

    public Gadugi(ClassLoader parent) {
        // parent will be sun.misc.Launcher$AppClassLoader - steal its URLs and its parent; in essence we're emulating it
        this(((URLClassLoader) parent).getURLs(), parent.getParent());
    }
    
    private Gadugi(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        init();
    }

    private void init() {
        // TODO - parse configuration and place in a LibraryVersion to ClassLoader map
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

    @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Object libraryVersion = get(name);
        // TODO - check libraryVersion against config map, delegating to the proper ClassLoader
        Class<?> clazz = super.loadClass(name, resolve);
        if (clazz.getClassLoader() == null) {
            System.out.format("^info^ loaded %s (with <boot> classloader) (using %s) on thread %s%n", name, libraryVersion,
                    Thread.currentThread().getName());
        } else {
            System.out.format("loaded %s (with %s classloader) (using %s) on thread %s%n", name, clazz.getClassLoader(),
                    libraryVersion, Thread.currentThread().getName());
        }
        return clazz;
    }

    /**
     * Note, returning {@linkplain Object} as the {@link LibraryVersion} within the class-loader is the boot-classloader's
     * version of the class and not that within the "user-land"'s {@link #using} variable.  It must be interacted
     * with in an opaque manner.
     * @param name of the {@link Class} which is being loaded.
     * @return the "user-land" value of the {@link #using} variable.
     */
    @SuppressWarnings("unchecked")
    private Object get(String name) {
        if ("net.ocheyedan.gadugi.Gadugi".equals(name)) { // prevent circular-reference-errors
            return null;
        }
        Class<?> userGadugi = findLoadedClass("net.ocheyedan.gadugi.Gadugi");
        if (userGadugi == null) {
            return null;
        }
        try {
            Field usingField = userGadugi.getDeclaredField("using");
            usingField.setAccessible(true);
            return ((ThreadLocal<Object>) usingField.get(null)).get();
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