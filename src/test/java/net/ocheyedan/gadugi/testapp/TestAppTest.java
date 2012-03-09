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
 * The unit test accompanying {@link TestApp}
 */
public class TestAppTest {

    @Test
    public void applicationUsage() throws IOException, InterruptedException {
        // TODO - give java command to process builder
        ProcessBuilder testapp = new ProcessBuilder(
                "java",
                "-cp",
                String.format("target/gadugi-1.0.jar%starget/gadugi-1.0-test.jar", File.pathSeparator),
                "-Djava.system.class.loader=net.ocheyedan.gadugi.Gadugi",
                "-Dgadugi.config=etc/gadugi-testapp/config.properties",
                "net.ocheyedan.gadugi.testapp.TestApp");
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
