package com.middleware.org.controller;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ValidationResult;
import com.middleware.org.service.IDataParseService;
import com.middleware.org.service.IDataCleanService;
import com.middleware.org.service.ServiceFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据处理控制器
 * 提供数据解析、清洗、校验相关的REST API接口
 */
@RestController
@RequestMapping("/api/data")
public class DataController {

    private final IDataParseService parseService;
    private final IDataCleanService cleanService;

    public DataController() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.parseService = factory.getDataParseService();
        this.cleanService = factory.getDataCleanService();
    }

    /**
     * 解析文件
     * POST /api/data/parse
     */
    @PostMapping("/parse")
    public Map<String, Object> parseFile(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String filePath = request.get("filePath");
            String fileType = request.get("fileType");

            List<DataRecord> records = parseService.parseFile(filePath, fileType);
            response.put("success", true);
            response.put("data", records);
            response.put("total", records.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "文件解析失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 清洗数据
     * POST /api/data/clean
     */
    @PostMapping("/clean")
    public Map<String, Object> cleanData(@RequestBody List<DataRecord> records) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DataRecord> cleanedRecords = cleanService.cleanData(records);
            response.put("success", true);
            response.put("data", cleanedRecords);
            response.put("total", cleanedRecords.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "数据清洗失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 校验数据
     * POST /api/data/validate
     */
    @PostMapping("/validate")
    public Map<String, Object> validateData(@RequestBody List<DataRecord> records) {
        Map<String, Object> response = new HashMap<>();
        try {
            ValidationResult result = cleanService.validateData(records);
            response.put("success", true);
            response.put("data", result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "数据校验失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 获取支持的文件类型
     * GET /api/data/supported-types
     */
    @GetMapping("/supported-types")
    public Map<String, Object> getSupportedTypes() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> types = parseService.getSupportedFileTypes();
            response.put("success", true);
            response.put("data", types);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取支持类型失败: " + e.getMessage());
        }
        return response;
    }
}
