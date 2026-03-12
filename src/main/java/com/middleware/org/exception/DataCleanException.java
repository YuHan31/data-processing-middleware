package com.middleware.org.exception;

/**
 * 数据清洗异常
 */
public class DataCleanException extends RuntimeException {

    public DataCleanException(String message) {
        super(message);
    }

    public DataCleanException(String message, Throwable cause) {
        super(message, cause);
    }
}
