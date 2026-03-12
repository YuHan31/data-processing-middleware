package com.middleware.org.service.impl;

import com.middleware.org.model.DataRecord;
import com.middleware.org.service.IDataParseService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据解析与格式转换服务实现
 */
public class DataParseServiceImpl implements IDataParseService {

    @Override
    public List<DataRecord> parseFile(String filePath, String fileType) {
        if (fileType == null || fileType.isEmpty()) {
            fileType = detectFileType(filePath);
        }

        switch (fileType.toUpperCase()) {
            case "CSV":
                return parseCsv(filePath);
            case "EXCEL":
                return parseExcel(filePath);
            case "JSON":
                return parseJson(filePath);
            default:
                throw new RuntimeException("不支持的文件类型: " + fileType);
        }
    }

    @Override
    public List<DataRecord> convertFormat(List<DataRecord> records, String targetFormat) {
        // 数据格式转换逻辑
        List<DataRecord> convertedRecords = new ArrayList<>();
        for (DataRecord record : records) {
            DataRecord converted = new DataRecord();
            converted.setFields(record.getFields());
            converted.setSourceType(targetFormat);
            convertedRecords.add(converted);
        }
        return convertedRecords;
    }

    @Override
    public List<String> getSupportedFileTypes() {
        return Arrays.asList("CSV", "EXCEL", "JSON", "XML");
    }

    private List<DataRecord> parseCsv(String filePath) {
        List<DataRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return records;
            }

            String[] headers = headerLine.split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                DataRecord record = new DataRecord();
                record.setSourceType("CSV");

                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.addField(headers[i].trim(), values[i].trim());
                }

                records.add(record);
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV文件解析失败: " + e.getMessage(), e);
        }

        return records;
    }

    private List<DataRecord> parseExcel(String filePath) {
        // Excel解析实现（需要Apache POI库）
        List<DataRecord> records = new ArrayList<>();
        // TODO: 实现Excel解析逻辑
        throw new RuntimeException("Excel解析功能待实现");
    }

    private List<DataRecord> parseJson(String filePath) {
        // JSON解析实现
        List<DataRecord> records = new ArrayList<>();
        // TODO: 实现JSON解析逻辑
        throw new RuntimeException("JSON解析功能待实现");
    }

    private String detectFileType(String filePath) {
        if (filePath.endsWith(".csv")) {
            return "CSV";
        } else if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
            return "EXCEL";
        } else if (filePath.endsWith(".json")) {
            return "JSON";
        }
        return "UNKNOWN";
    }
}
