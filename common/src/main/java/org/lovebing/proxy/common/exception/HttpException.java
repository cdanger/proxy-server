package org.lovebing.proxy.common.exception;

/**
 * @author lovebing Created on Apr 3, 2017
 */
public class HttpException extends Exception {

    protected int code;
    public HttpException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
