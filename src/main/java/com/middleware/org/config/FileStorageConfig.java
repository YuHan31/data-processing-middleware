package com.middleware.org.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * 文件存储配置
 */
@Configuration
public class FileStorageConfig {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.output.path:./outputs}")
    private String outputPath;

    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(uploadPath);
        createDirectoryIfNotExists(outputPath);
    }

    private void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public String getOutputPath() {
        return outputPath;
    }
}
