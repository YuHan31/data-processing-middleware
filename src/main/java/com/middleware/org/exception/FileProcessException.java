package com.middleware.org.exception;

/**
 * 文件处理异常
 */
public class FileProcessException extends RuntimeException {

    public FileProcessException(String message) {
        super(message);
    }

    public FileProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
