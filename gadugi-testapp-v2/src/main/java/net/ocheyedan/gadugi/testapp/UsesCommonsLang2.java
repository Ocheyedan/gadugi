package net.ocheyedan.gadugi.testapp;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Date: 3/9/12
 * Time: 7:37 AM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * Uses {@literal Apache}'s {@literal commons-lang:commons-lang:2.0}.  Used by the {@literal gadugi-testapp} project
 * along with {@literal gadugi-testapp-v1}.
 */
public class UsesCommonsLang2 {
    
    public static String chomp(String value) {
        // note, ArrayUtils does not exist in commons-lang:1.0
        char[] chars = ArrayUtils.clone(value.toCharArray());
        return StringUtils.chomp(new String(chars));
    }
    
}
