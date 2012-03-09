package net.ocheyedan.gadugi.testapp;

import org.apache.commons.lang.StringUtils;

/**
 * Date: 3/9/12
 * Time: 7:37 AM
 * @author Trevor Smith
 * @author Brian Langel
 *
 * Uses {@literal Apache}'s {@literal commons-lang:commons-lang:1.0}.  Used by the {@literal gadugi-testapp} project
 * along with {@literal gadugi-testapp-v2}.
 */
public class UsesCommonsLang1 {

    public static String chomp(String value) {
        return StringUtils.chomp(value);
    }

}
