package com.middleware.org.controller;

import com.middleware.org.model.FileMetadata;
import com.middleware.org.service.IFilePreprocessService;
import com.middleware.org.service.ServiceFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理控制器
 * 提供文件上传、预处理相关的REST API接口
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final IFilePreprocessService fileService;

    public FileController() {
        this.fileService = ServiceFactory.getInstance().getFilePreprocessService();
    }

    /**
     * 上传文件
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            FileMetadata metadata = fileService.uploadFile(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
            response.put("success", true);
            response.put("data", metadata);
            response.put("message", "文件上传成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 验证文件格式
     * POST /api/files/validate
     */
    @PostMapping("/validate")
    public Map<String, Object> validateFile(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String filePath = request.get("filePath");
            boolean isValid = fileService.validateFileFormat(filePath);
            response.put("success", true);
            response.put("valid", isValid);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "文件验证失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 获取文件元数据
     * GET /api/files/metadata
     */
    @GetMapping("/metadata")
    public Map<String, Object> getFileMetadata(@RequestParam String filePath) {
        Map<String, Object> response = new HashMap<>();
        try {
            FileMetadata metadata = fileService.getFileMetadata(filePath);
            response.put("success", true);
            response.put("data", metadata);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取文件元数据失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 预处理文件
     * POST /api/files/preprocess
     */
    @PostMapping("/preprocess")
    public Map<String, Object> preprocessFile(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String filePath = request.get("filePath");
            String processedPath = fileService.preprocessFile(filePath);
            response.put("success", true);
            response.put("processedPath", processedPath);
            response.put("message", "文件预处理成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "文件预处理失败: " + e.getMessage());
        }
        return response;
    }
}
