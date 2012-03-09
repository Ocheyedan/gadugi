package net.ocheyedan.gadugi.testapp;

import net.ocheyedan.gadugi.Gadugi;

/**
 * Date: 3/9/12
 * Time: 8:14 AM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * A test client to be invoked and run by a unit test to ensure {@literal Gadugi} functions within an application.
 * Specifically this tests the valid scenario in which the user has called {@link net.ocheyedan.gadugi.Gadugi#using(String)}
 * correctly before calling dependent libraries.
 */
public class TestAppSuccess {

    public static void main(String[] args) {

        System.out.format("%n%nStarting TestAppSuccess test.%n%n");

        Gadugi.using("lang1");
        String result = UsesCommonsLang1.chomp("some value\n");
        if (!"some value".equals(result)) {
            System.out.format("Expected \"%s\" but was \"%s\".%n", "some value", result);
            System.out.format("%n%nFailing TestAppSuccess test.%n%n");
            System.exit(1);
        }

        Gadugi.using("lang2");
        result = UsesCommonsLang2.chomp("v2 some value\n");
        if (!"v2 some value".equals(result)) {
            System.out.format("Expected \"%s\" but was \"%s\".%n", "v2 some value", result);
            System.out.format("%n%nFailing TestAppSuccess test.%n%n");
            System.exit(1);
        }

        System.out.format("%n%nEnding TestAppSuccess test.%n%n");
    }

}
