package net.ocheyedan.gadugi;

import org.junit.Test;

import java.lang.reflect.Field;

import static junit.framework.Assert.*;

/**
 * Date: 3/2/12
 * Time: 4:17 PM
 * @author Trevor Smith
 * @author Brian Langel
 */
public class GadugiTest {

    @Test @SuppressWarnings("unchecked")
    public void using() throws NoSuchFieldException, IllegalAccessException {
        Field usingField = Gadugi.class.getDeclaredField("using");
        usingField.setAccessible(true);

        assertNull(((ThreadLocal<String>) usingField.get(null)).get());
        Gadugi.using("test");
        assertEquals("test", ((ThreadLocal<String>) usingField.get(null)).get());
    }

}
