package org.hon.log.analysis.search.util

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import com.google.common.net.InternetDomainName;

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

    public static String getDomainFromUrl(String url) {
        if (url) {

            try {
                String host = new URI(url).getHost()

                if (host) {

                    try {
                        InternetDomainName domainName = InternetDomainName.fromLenient(host)
                        return domainName.topPrivateDomain().name()
                    } catch (IllegalArgumentException ignore) {}// invalid domain, such as 129.195.254.166
                }

            } catch (URISyntaxException ignore) {}  // e.g. Illegal character in query at index 56:
                                                    // http://debussy.hon.ch/cgi-bin/HONselect_f?anatomie_cavit\xc3\xa9_buccale
        }
        return null;
    }
}
