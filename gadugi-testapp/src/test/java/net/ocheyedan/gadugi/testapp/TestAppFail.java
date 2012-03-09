package net.ocheyedan.gadugi.testapp;

/**
 * Date: 3/8/12
 * Time: 1:58 PM
 * @author Trevor Smith
 * @author Brian Langel
 * 
 * A test client to be invoked and run by a unit test to ensure {@literal Gadugi} functions within an application.
 * Specifically this tests that if the user hasn't called {@link net.ocheyedan.gadugi.Gadugi#using(String)} before 
 * calling dependent libraries the system fails.
 */
public class TestAppFail {
    
    public static void main(String[] args) {

        System.out.format("%n%nStarting TestAppFail test.%n%n");

        try {
            UsesCommonsLang1.chomp("some value; should fail as we haven't called Gadugi.using()");
            System.exit(1);
        } catch (NoClassDefFoundError ncdfe) {
            ncdfe.printStackTrace();
            if (!"org/apache/commons/lang/StringUtils".equals(ncdfe.getMessage())) {
                System.out.format("Expected [ %s ] was [ %s ]%n", "org/apache/commons/lang/StringUtils", ncdfe.getMessage());
                System.out.format("%n%nFailing TestAppFail test.%n%n");
                System.exit(1);
            }
        }

        try {
            UsesCommonsLang2.chomp("some value; should fail as we haven't called Gadugi.using()");
            System.exit(1);
        } catch (NoClassDefFoundError ncdfe) {
            ncdfe.printStackTrace();
            if (!"org/apache/commons/lang/ArrayUtils".equals(ncdfe.getMessage())) {
                System.out.format("Expected [ %s ] was [ %s ]%n", "org/apache/commons/lang/ArrayUtils", ncdfe.getMessage());
                System.out.format("%n%nFailing TestAppFail test.%n%n");
                System.exit(1);
            }
        }

        System.out.format("%n%nEnding TestAppFail test.%n%n");

    }
    
}
