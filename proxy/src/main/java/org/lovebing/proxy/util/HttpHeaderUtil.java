package org.lovebing.proxy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lovebing Created on Apr 4, 2017
 */
public class HttpHeaderUtil {

    public static long[] getRange(String value) {
        long[] range = new long[2];
        if (value == null) {
            return range;
        }
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(value);

        int index = 0;
        while (matcher.find()) {
            range[index] = Long.valueOf(matcher.group());
            index++;
            if (index >= range.length) {
                break;
            }
        }
        return range;
    }

    public static String responseRangeValue(long[] range, long totalSize) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("bytes ");
        stringBuffer.append(range[0]);
        stringBuffer.append("-");
        if (range[1] > 0) {
            stringBuffer.append(range[1]);
        }
        stringBuffer.append("/");
        stringBuffer.append(totalSize);
        return stringBuffer.toString();
    }
}
