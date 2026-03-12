package com.middleware.org.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileProcessException.class)
    public ResponseEntity<Map<String, Object>> handleFileProcessException(FileProcessException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "文件处理失败", e.getMessage());
    }

    @ExceptionHandler(DataParseException.class)
    public ResponseEntity<Map<String, Object>> handleDataParseException(DataParseException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "数据解析失败", e.getMessage());
    }

    @ExceptionHandler(DataCleanException.class)
    public ResponseEntity<Map<String, Object>> handleDataCleanException(DataCleanException e) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "数据清洗失败", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "系统错误", e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(response, status);
    }
}
