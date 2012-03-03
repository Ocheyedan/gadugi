package net.ocheyedan.gadugi;

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
        System.out.format("setting %s on thread %s%n", (libraryVersion == null ? "<null>" : libraryVersion.toString()), Thread.currentThread().getName());
        using.set(libraryVersion);
    }

    public Gadugi(ClassLoader parent) {
        // parent will be sun.misc.Launcher$AppClassLoader - steal its URLs and its parent; in essence we're emulating it
        this(((URLClassLoader) parent).getURLs(), parent.getParent());
    }
    
    public Gadugi(URL[] urls, ClassLoader parent) {
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
        Class<?> clazz = super.loadClass(name, resolve);
        if (clazz.getClassLoader() == null) {
            System.out.format("^info^ loaded %s (with <boot> classloader) (using %s) on thread %s%n", name, using.get(),
                    Thread.currentThread().getName());
        } else {
            System.out.format("loaded %s (with %s classloader) (using %s) on thread %s%n", name, clazz.getClassLoader(),
                    using.get(), Thread.currentThread().getName());
        }
        return clazz;
    }
    
    @Override public String toString() {
        return "Gadugi!";
    }

}