package net.ocheyedan.gadugi;

/**
 * Date: 3/3/12
 * Time: 11:11 AM
 * @author Trevor Smith
 * @author Brian Langel
 * 
 * Exists as a test application loaded with {@link Gadugi} as the system-classloader.
 */
public class GadugiTestApp {
    
    public static void main(String[] args) {
        Gadugi.using("thrift-version-2");
        Thrift2Code thrift2Code = new Thrift2Code();
        System.out.println(thrift2Code.toString());
        Gadugi.using("thrift-version-3");
        Thrift3Code thrift3Code = new Thrift3Code();
        System.out.println(thrift3Code.toString());
    }
    
}
