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

    /**
     * Ensure calling code has properly set {@link Gadugi} as the system class loader.
     */
    static {
        if (!(Thread.currentThread().getContextClassLoader() instanceof Gadugi)) {
            throw new AssertionError("Set Gadugi as the system class-loader: -Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi");
        }
    }
    
    /**
     * Sets the library version to use for the calling thread's subsequent invocations.
     * @param libraryVersion of the library to use on the calling thread.
     */
    public static void using(LibraryVersion libraryVersion) {
        // usage is to set Gadugi as the system classloader, so get the instance and set
        ((Gadugi) Thread.currentThread().getContextClassLoader()).using.set(libraryVersion);
    }

    private final ThreadLocal<LibraryVersion> using = new ThreadLocal<LibraryVersion>();

    public Gadugi(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        init();
    }

    public Gadugi(URL[] urls) {
        super(urls);
        init();
    }

    public Gadugi(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        init();
    }

    private void init() {
        // TODO - parse configuration and place in a LibraryVersion to ClassLoader map
    }

    // TODO - override methods to complete the delegation given the values of {@link #using}

}