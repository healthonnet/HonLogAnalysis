package org.hon.log.analysis.search.util

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

class URLUtil {

    /**
     *
     * Decode without throwing so many damn IllegalArgumentExceptions.  Just ignore the bad tokens,
     * leave them in as they are, and keep on truckin'.
     *
     * @param s
     * @param enc
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decodeLenient(String s, String enc) throws UnsupportedEncodingException {

        if (!s) {
            return null;
        }
        
        return StringUtil.nonStupidSplit(s, '+').collect { token ->
            
            try {
                return URLDecoder.decode(token, enc);
            } catch (IllegalArgumentException ignore) {
                return token; // return the original, unencoded value
            }
            
        }.join(' ')
        
    }
}
