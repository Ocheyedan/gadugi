package net.ocheyedan.gadugi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Date: 3/4/12
 * Time: 10:07 AM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * Parses the {@literal properties} file used to configure the library versions.  The file should be on the classpath
 * at the location specified with system property {@literal gadugi.config}.  For instance, to specify that the
 * configuration file is accessible on the classpath as {@literal etc/myapp/gadugi.config} then invoke your {@literal JVM}
 * with the following system property:
 * <pre>
 *     -Dgadugi.config=etc/myapp/gadugi.config
 * </pre>
 */
final class Configuration {

    @SuppressWarnings("unchecked")
    public static Map<String, String> get() {
        
        String gadugiConfigLocation = System.getProperty("gadugi.config");
        if (gadugiConfigLocation == null) {
            throw new AssertionError("Could not find the gadugi configuration file, set via -Dgadugi.config=xxxx%n");
        }
        ClassLoader loader = (Configuration.class.getClassLoader() == null
                                ? ClassLoader.getSystemClassLoader() : Configuration.class.getClassLoader());
        InputStream config = loader.getResourceAsStream(gadugiConfigLocation);
        if (config == null) {
            throw new IllegalStateException(String.format("Could not load the given 'gadugi.config' file [ %s ].", gadugiConfigLocation));
        }
        Properties libraryVersions = new Properties();
        try {
            libraryVersions.load(config);
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Error loading %s", gadugiConfigLocation), t);
        }
        return convert(libraryVersions);
    }

    /**
     * @param properties to convert
     * @return {@code properties} into a {@link Map} object
     */
    private static Map<String, String> convert(Properties properties) {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }
    
    private Configuration() { }

}
