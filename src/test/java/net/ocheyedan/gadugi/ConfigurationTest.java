package net.ocheyedan.gadugi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Date: 3/8/12
 * Time: 8:41 AM
 * @author Trevor Smith
 * @author Brian Langel
 */
public class ConfigurationTest {

    @Before
    public void setup() {
        System.getProperties().remove("gadugi.config");
    }

    @After
    public void teardown() {
        System.getProperties().remove("gadugi.config");
    }

    @Test
    public void get() {

        // test not present
        try {
            Configuration.get();
            fail("Expecting an AssertionError as there was no 'gadugi.config' property defined.");
        } catch (AssertionError ae) {
            // expected
        }

        // test a not present file
        try {
            System.setProperty("gadugi.config", "does not exist");
            Configuration.get();
            fail("Expecting an IllegalStateException as the file isn't present on the classpath.");
        } catch (IllegalStateException ise) {
            // expected
        }
        
        // test an invalid properties file
        try {
            System.setProperty("gadugi.config", "etc/gadugi/notapropertiesfile.config");
            Map<String, String> map = Configuration.get();
            System.out.format("Map = %s%n", map.toString());
            fail("Expecting a RuntimeException as the file isn't a properties file.");
        } catch (RuntimeException re) {
            // expected
        }
        
        // test a valid properties file
        System.setProperty("gadugi.config", "etc/gadugi/mock-1.properties");
        Map<String, String> map = Configuration.get();
        assertEquals("thrift-v1.jar", map.get("thriftv1"));
        assertEquals("thrift-v2.jar", map.get("thriftv2"));
        assertEquals("thrift-v3.jar", map.get("thriftv3"));
        assertEquals("thrift-v4.jar", map.get("thriftv4"));

    }

}
