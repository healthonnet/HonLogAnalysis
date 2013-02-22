package org.hon.log.analysis.search.util;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.primitives.Bytes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nolan
 * Date: 2/22/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

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

        return result;
    }

    /**
     * Replace any invalid UTF8 characters, such as this guy: http://www.fileformat.info/info/unicode/char/10007a/index.htm
     *
     * It short, this replaces all 4-byte sequences that start with 11110xxx with "<?>" ("\uFFFD"),
     * since MySQL (as of version 5.1) does not support 4-byte UTF8 characters
     * Ugh... mysql!
     *
     * Needed to fix KHRES-328
     * @param input
     * @return
     */
    public static String replaceFourByteUtf8(String input) {

        try {
            byte[] bytes = input.getBytes(Charsets.UTF_8.name());

            // most of the time, replacement will not be necessary, so check first
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                if (isFourByteBeginMarker(b)) { // four-byte begin marker
                    return replaceFourByteUtf8Internal(bytes);
                }
            }
            return input;
        } catch (UnsupportedEncodingException e) { // should never occur
            throw Throwables.propagate(e);
        }
    }

    private static String replaceFourByteUtf8Internal(byte[] bytes) throws UnsupportedEncodingException {
        List<Byte> byteList = new ArrayList<Byte>(Bytes.asList(bytes));

        for (int i = 0 ; i < byteList.size(); i++) {
            Byte b = byteList.get(i);
            if (isFourByteBeginMarker(b)) {
                byteList.remove(i);
                // unknown character, i.e.  <?>, aka [e0, b0, b0] aka "\uFFFD"
                byteList.set(i, (byte)-17);
                byteList.set(i + 1, (byte)-65);
                byteList.set(i + 2, (byte)-67);
            }
        }

        return new String(Bytes.toArray(byteList), Charsets.UTF_8.name());
    }

    private static boolean isFourByteBeginMarker(byte b) {
        // true if it matches 11110xxx
        return ((b >> 3) & 0x1F) == 0x1E;
    }

}
