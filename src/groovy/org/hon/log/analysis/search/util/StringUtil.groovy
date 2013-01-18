package org.hon.log.analysis.search.util

import org.codehaus.groovy.runtime.ArrayUtil

class StringUtil {

    /**
     * Split a string without the bullshit that Java does, where if the delimiter falls at the end, you get nothing
     * instead of the empty string.  
     * 
     * I probably should use Google Guava's Splitter() like a sane person, but I don't feel like including 
     * a 1MB extra jar file in the code right now.
     * 
     * 
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> nonStupidSplit(String str, String delimiter) {
        
        List<String> result = new ArrayList<String>();
        int lastIndex = 0;
        int index = str.indexOf(delimiter);
        while (index != -1) {
            result.add(str.substring(lastIndex, index));
            lastIndex = index + delimiter.length();
            index = str.indexOf(delimiter, index + delimiter.length());
        }
        result.add(str.substring(lastIndex, str.length()));

        return result
    }
    
}
