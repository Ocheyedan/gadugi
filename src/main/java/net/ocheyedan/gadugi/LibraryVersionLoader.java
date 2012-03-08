package net.ocheyedan.gadugi;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Date: 3/4/12
 * Time: 5:57 PM
 * @author Trevor Smith
 * @author Brian Langel
 * 
 * Each library version within the system will have its own {@link LibraryVersionLoader} which will contain a reference
 * to the library version jar.  The {@link #parent} should be {@link Gadugi} itself to properly participate in
 * delegation for future class load attempts.
 */
final class LibraryVersionLoader extends URLClassLoader {
    
    private final String identifier;

    LibraryVersionLoader(URL[] urls, ClassLoader parent, String libraryVersion) {
        super(urls, parent);
        this.identifier = "Gadugi-" + libraryVersion;
    }

    /**
     * Allow package classes access to the {@link #loadClass(String, boolean)} method.
     * @param name @see {@link #loadClass(String, boolean)}
     * @param resolve @see {@link #loadClass(String, boolean)}
     * @return @see {@link #loadClass(String, boolean)}
     * @throws ClassNotFoundException @see {@link #loadClass(String, boolean)}
     */
    Class<?> _loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass(name, resolve);
    }

    @Override public String toString() {
        return identifier;
    }
}
