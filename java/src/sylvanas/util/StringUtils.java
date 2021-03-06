package sylvanas.util;

import java.io.UnsupportedEncodingException;

/**
 * @Description:
 */
public class StringUtils {

    public static boolean isEmpty(String string){
        if (string==null||string.trim().length()==0){
            return true;
        }
        return false;
    }

    public static String cleanPath(String string){
        if (string.startsWith("/")){
            string = string.substring(1);
        }
        return string;
    }

    public static byte[] getBytes(String src){
        try {
            return src.getBytes(Constants.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("un sport exception");
        }
    }

}
