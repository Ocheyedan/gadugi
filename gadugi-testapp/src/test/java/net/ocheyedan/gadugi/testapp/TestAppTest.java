package net.ocheyedan.gadugi.testapp;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertEquals;

/**
 * Date: 3/8/12
 * Time: 1:59 PM
 * @author Trevor Smith
 * @author Brian Langel
 * 
 * The unit test accompanying {@link TestAppFail}
 */
public class TestAppTest {

    @Test
    public void applicationUsageFail() throws IOException, InterruptedException {
        // failure case (user did not call Gadugi.using())
        ProcessBuilder testapp = new ProcessBuilder(
                "java",
                "-cp",
                String.format("target/gadugi-testapp-1.0-test.jar%s../gadugi-testapp-v1/target/gadugi-testapp-v1-1.0.jar"
                        + "%s../gadugi-testapp-v2/target/gadugi-testapp-v2-1.0.jar%s../target/gadugi-1.0.jar",
                        File.pathSeparator, File.pathSeparator, File.pathSeparator),
                "-Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi",
                "-Dgadugi.config=etc/gadugi-testapp/config.properties",
                "net.ocheyedan.gadugi.testapp.TestAppFail");
        testapp.redirectErrorStream(true);
        Process process = testapp.start();

        BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String processStdoutLine;
        while ((processStdoutLine = processStdout.readLine()) != null) {
            System.out.format("%s%n", processStdoutLine);
        }

        int exitCode = process.waitFor();
        assertEquals(0, exitCode);
    }

    @Test
    public void applicationUsageSuccess() throws IOException, InterruptedException {
        // success case (user did call Gadugi.using())
        ProcessBuilder testapp = new ProcessBuilder(
                "java",
                "-cp",
                String.format("target/gadugi-testapp-1.0-test.jar%s../gadugi-testapp-v1/target/gadugi-testapp-v1-1.0.jar"
                        + "%s../gadugi-testapp-v2/target/gadugi-testapp-v2-1.0.jar%s../target/gadugi-1.0.jar",
                        File.pathSeparator, File.pathSeparator, File.pathSeparator),
                "-Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi",
                "-Dgadugi.config=etc/gadugi-testapp/config.properties",
                "net.ocheyedan.gadugi.testapp.TestAppSuccess");
        testapp.redirectErrorStream(true);
        Process process = testapp.start();

        BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String processStdoutLine;
        while ((processStdoutLine = processStdout.readLine()) != null) {
            System.out.format("%s%n", processStdoutLine);
        }

        int exitCode = process.waitFor();
        assertEquals(0, exitCode);
    }

    @Test
    public void applicationUsageVersionFail() throws IOException, InterruptedException {
        // success case (user did call Gadugi.using())
        ProcessBuilder testapp = new ProcessBuilder(
                "java",
                "-cp",
                String.format("target/gadugi-testapp-1.0-test.jar%s../gadugi-testapp-v1/target/gadugi-testapp-v1-1.0.jar"
                        + "%s../gadugi-testapp-v2/target/gadugi-testapp-v2-1.0.jar%s../target/gadugi-1.0.jar",
                        File.pathSeparator, File.pathSeparator, File.pathSeparator),
                "-Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi",
                "-Dgadugi.config=etc/gadugi-testapp/config.properties",
                "net.ocheyedan.gadugi.testapp.TestAppVersionFail");
        testapp.redirectErrorStream(true);
        Process process = testapp.start();

        BufferedReader processStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String processStdoutLine;
        while ((processStdoutLine = processStdout.readLine()) != null) {
            System.out.format("%s%n", processStdoutLine);
        }

        int exitCode = process.waitFor();
        assertEquals(0, exitCode);
    }

}
