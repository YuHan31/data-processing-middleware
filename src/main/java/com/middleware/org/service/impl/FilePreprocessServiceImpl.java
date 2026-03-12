package com.middleware.org.service.impl;

import com.middleware.org.model.FileMetadata;
import com.middleware.org.service.IFilePreprocessService;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件上传与预处理服务实现
 */
public class FilePreprocessServiceImpl implements IFilePreprocessService {

    @Override
    public FileMetadata uploadFile(InputStream inputStream, String fileName) {
        try {
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filePath = uploadDir + fileName;
            Files.copy(inputStream, Paths.get(filePath));

            FileMetadata metadata = new FileMetadata(fileName, filePath, detectFileType(fileName));
            metadata.setFileSize(new File(filePath).length());
            metadata.setEncoding(detectEncoding(filePath));

            return metadata;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateFileFormat(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        String fileType = detectFileType(filePath);
        return fileType != null && !fileType.isEmpty();
    }

    @Override
    public FileMetadata getFileMetadata(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在: " + filePath);
        }

        FileMetadata metadata = new FileMetadata(file.getName(), filePath, detectFileType(filePath));
        metadata.setFileSize(file.length());
        metadata.setEncoding(detectEncoding(filePath));

        return metadata;
    }

    @Override
    public String preprocessFile(String filePath) {
        if (!validateFileFormat(filePath)) {
            throw new RuntimeException("文件格式无效: " + filePath);
        }

        // 检测编码并转换为UTF-8
        String encoding = detectEncoding(filePath);
        if (!"UTF-8".equalsIgnoreCase(encoding)) {
            return convertToUtf8(filePath, encoding);
        }

        return filePath;
    }

    private String detectFileType(String fileName) {
        if (fileName.endsWith(".csv")) {
            return "CSV";
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return "EXCEL";
        } else if (fileName.endsWith(".json")) {
            return "JSON";
        } else if (fileName.endsWith(".xml")) {
            return "XML";
        }
        return "UNKNOWN";
    }

    private String detectEncoding(String filePath) {
        try (InputStream is = new FileInputStream(filePath)) {
            byte[] buffer = new byte[4096];
            int read = is.read(buffer);

            // 简单的编码检测逻辑
            if (read > 3 && buffer[0] == (byte) 0xEF && buffer[1] == (byte) 0xBB && buffer[2] == (byte) 0xBF) {
                return "UTF-8";
            }

            return Charset.defaultCharset().name();
        } catch (IOException e) {
            return "UTF-8";
        }
    }

    private String convertToUtf8(String filePath, String sourceEncoding) {
        try {
            String outputPath = filePath + ".utf8";
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath), sourceEncoding));
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outputPath), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();

            return outputPath;
        } catch (IOException e) {
            throw new RuntimeException("编码转换失败: " + e.getMessage(), e);
        }
    }
}
