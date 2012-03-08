package net.ocheyedan.gadugi;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import static junit.framework.Assert.*;

/**
 * Date: 3/2/12
 * Time: 4:17 PM
 * @author Trevor Smith
 * @author Brian Langel
 */
public class GadugiTest {
    
    @After
    public void teardown() {
        System.getProperties().remove("gadugi.config");
    }
    

    @Test @SuppressWarnings("unchecked")
    public void using() throws NoSuchFieldException, IllegalAccessException {
        Field usingField = Gadugi.class.getDeclaredField("using");
        usingField.setAccessible(true);

        assertNull(((ThreadLocal<String>) usingField.get(null)).get());
        Gadugi.using("test");
        assertEquals("test", ((ThreadLocal<String>) usingField.get(null)).get());
    }

    @Test @SuppressWarnings("unchecked")
    public void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException,
            InvocationTargetException {
        System.setProperty("gadugi.config", "etc/gadugi/mock-1.properties");

        Gadugi gadugi = new Gadugi(GadugiTest.class.getClassLoader());
        
        // private void init()
        Method initMethod = Gadugi.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        // private final ConcurrentMap<String, LibraryVersionLoader> classLoaders
        Field classLoadersField = Gadugi.class.getDeclaredField("classLoaders");
        classLoadersField.setAccessible(true);
        assertEquals(0, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());

        initMethod.invoke(gadugi);

        assertEquals(4, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
        assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv1"));
        assertEquals("Gadugi-thriftv1",
                ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv1")
                        .toString());
        assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv2"));
        assertEquals("Gadugi-thriftv2", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv2").toString());
        assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv3"));
        assertEquals("Gadugi-thriftv3", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv3").toString());
        assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv4"));
        assertEquals("Gadugi-thriftv4", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv4").toString());
    }
    
    @Test
    public void create() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // private LibraryVersionLoader create(String libraryVersionId, String libraryVersionLocation)
        Method createMethod = Gadugi.class.getDeclaredMethod("create", String.class, String.class);
        createMethod.setAccessible(true);

        Gadugi gadugi = new Gadugi(GadugiTest.class.getClassLoader());

        // test malformed
        LibraryVersionLoader returned;
        try {
            returned = (LibraryVersionLoader) createMethod.invoke(gadugi, "id", null);
            fail(String.format("Expected a RuntimeException but got %s.", returned.toString()));
        } catch (InvocationTargetException ite) {
            if (ite.getTargetException().getClass() != RuntimeException.class) {
                fail(String.format("Expected a RuntimeException but got exception %s [ %s ].",
                        ite.getTargetException().getClass(), ite.getTargetException().getMessage()));
            }
        }

        // test well-formed
        returned = (LibraryVersionLoader) createMethod.invoke(gadugi, "id", "mock-v1.jar");
        assertEquals("Gadugi-id", returned.toString());
    }

    @Test @SuppressWarnings("unchecked")
    public void loadClass() throws NoSuchFieldException, IllegalAccessException {
        Gadugi gadugi = new Gadugi(GadugiTest.class.getClassLoader());

        // ensure java classes are skipped altogether
        Field classLoadersField = Gadugi.class.getDeclaredField("classLoaders");
        classLoadersField.setAccessible(true);
        assertEquals(0, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
        try {
            Class<?> setClass = gadugi.loadClass("java.util.Set");
            assertSame(Set.class, setClass);
            assertEquals(0, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
        } catch (ClassNotFoundException cnfe) {
            // class should be on the classpath, it's a java standard class
            fail(cnfe.getMessage());
        }

        // test that classes already added into the 'gadugiEncountered' map skip other checks/init/etc
        Field  gadugiEncounteredField = Gadugi.class.getDeclaredField("gadugiEncountered");
        gadugiEncounteredField.setAccessible(true);
        assertEquals(0, ((ConcurrentMap<?, ?>) gadugiEncounteredField.get(gadugi)).size());
        assertEquals(0, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
        // force add into the 'gadugiEncountered' field so that we can test calls to loadClass are short-circuited
        ((ConcurrentMap<String, String>) gadugiEncounteredField.get(gadugi)).put("test", "");

        try {
            gadugi.loadClass("test", false);
        } catch (ClassNotFoundException cnfe) {
            // ensure init wasn't called
            assertEquals(0, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
            // ensure that whatever has happened, 'gadugiEncountered' doesn't still contain 'test'
            assertEquals(0, ((ConcurrentMap<?, ?>) gadugiEncounteredField.get(gadugi)).size());
        }

        // test that first load calls init
        assertEquals(0, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
        try {
            System.setProperty("gadugi.config", "etc/gadugi/mock-1.properties");
            gadugi.loadClass("not a class", false);
        } catch (ClassNotFoundException cnfe) {
            // expected exception, check that init was called
            assertEquals(4, ((ConcurrentMap<?, ?>) classLoadersField.get(gadugi)).size());
            assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv1"));
            assertEquals("Gadugi-thriftv1", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv1").toString());
            assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv2"));
            assertEquals("Gadugi-thriftv2", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv2").toString());
            assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv3"));
            assertEquals("Gadugi-thriftv3", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv3").toString());
            assertNotNull(((ConcurrentMap<String, ?>) classLoadersField.get(gadugi)).get("thriftv4"));
            assertEquals("Gadugi-thriftv4", ((ConcurrentMap<String, LibraryVersionLoader>) classLoadersField.get(gadugi)).get("thriftv4").toString());
        }

    }

}
