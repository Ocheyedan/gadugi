package net.ocheyedan.gadugi.testapp;

import net.ocheyedan.gadugi.Gadugi;

/**
 * Date: 3/9/12
 * Time: 8:32 AM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * A test client to be invoked and run by a unit test to ensure {@literal Gadugi} functions within an application.
 * Specifically this tests that if the user hasn't called {@link net.ocheyedan.gadugi.Gadugi#using(String)} again
 * before calling new versions of dependent libraries the system fails.
 */
public class TestAppVersionFail {

    public static void main(String[] args) {

        System.out.format("%n%nStarting TestAppVersionFail test.%n%n");

        Gadugi.using("lang1");
        String result = UsesCommonsLang1.chomp("some value\n");
        if (!"some value".equals(result)) {
            System.out.format("Expected \"%s\" but was \"%s\".%n", "some value", result);
            System.out.format("%n%nFailing TestAppVersionFail test.%n%n");
            System.exit(1);
        }

        // Note, not calling Gadugi.using("lang2") here
        try {
            UsesCommonsLang2.chomp("some value; should fail as we haven't called Gadugi.using() for the new version");
            System.exit(1);
        } catch (NoClassDefFoundError ncdfe) {
            ncdfe.printStackTrace();
            if (!"org/apache/commons/lang/ArrayUtils".equals(ncdfe.getMessage())) {
                System.out.format("Expected [ %s ] was [ %s ]%n", "org/apache/commons/lang/ArrayUtils", ncdfe.getMessage());
                System.out.format("%n%nFailing TestAppVersionFail test.%n%n");
                System.exit(1);
            }
        }

        System.out.format("%n%nEnding TestAppVersionFail test.%n%n");

    }

}
