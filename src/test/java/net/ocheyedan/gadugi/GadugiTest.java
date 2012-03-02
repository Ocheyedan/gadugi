package net.ocheyedan.gadugi;

import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * Date: 3/2/12
 * Time: 4:17 PM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * TODO - how to test as {@literal Gadugi} depends upon being set as the system classloader?
 */
public class GadugiTest {

    @Test
    public void assertSystemClassloader() {
        try {
            Gadugi.using(null);
            fail("Expected AssertionError as we're not testing (yet?) with Gadugi as the system classloader.");
        } catch (AssertionError ae) {
            // expected
        }
    }

}
