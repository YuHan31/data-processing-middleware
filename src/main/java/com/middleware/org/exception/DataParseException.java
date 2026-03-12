package com.middleware.org.exception;

/**
 * 数据解析异常
 */
public class DataParseException extends RuntimeException {

    public DataParseException(String message) {
        super(message);
    }

    public DataParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
