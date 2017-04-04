package org.lovebing.proxy;

import org.junit.Test;
import org.lovebing.proxy.util.HttpHeaderUtil;

/**
 * @author lovebing Created on Apr 4, 2017
 */
public class HttpHeaderTest {

    @Test
    public void rangeTest() {
        String value = "bytes=83135-";

        long[] range = HttpHeaderUtil.getRange(value);

        System.out.print(range[0] + "," + range[1]);
    }
}
