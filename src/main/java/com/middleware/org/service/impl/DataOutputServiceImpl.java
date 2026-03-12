package com.middleware.org.service.impl;

import com.middleware.org.model.DataRecord;
import com.middleware.org.service.IDataOutputService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 结果输出适配服务实现
 */
public class DataOutputServiceImpl implements IDataOutputService {

    @Override
    public boolean outputToCsv(List<DataRecord> records, String outputPath) {
        if (records == null || records.isEmpty()) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // 写入表头
            DataRecord firstRecord = records.get(0);
            String header = String.join(",", firstRecord.getFields().keySet());
            writer.write(header);
            writer.newLine();

            // 写入数据行
            for (DataRecord record : records) {
                StringBuilder line = new StringBuilder();
                int i = 0;
                for (Object value : record.getFields().values()) {
                    if (i > 0) {
                        line.append(",");
                    }
                    line.append(value != null ? value.toString() : "");
                    i++;
                }
                writer.write(line.toString());
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            throw new RuntimeException("CSV输出失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean outputToExcel(List<DataRecord> records, String outputPath) {
        // Excel输出实现（需要Apache POI库）
        throw new RuntimeException("Excel输出功能待实现");
    }

    @Override
    public boolean outputToJson(List<DataRecord> records, String outputPath) {
        // JSON输出实现
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("[");
            writer.newLine();

            for (int i = 0; i < records.size(); i++) {
                DataRecord record = records.get(i);
                writer.write("  {");
                writer.newLine();

                int j = 0;
                for (Map.Entry<String, Object> entry : record.getFields().entrySet()) {
                    writer.write("    \"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                    if (j < record.getFields().size() - 1) {
                        writer.write(",");
                    }
                    writer.newLine();
                    j++;
                }

                writer.write("  }");
                if (i < records.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }

            writer.write("]");
            return true;
        } catch (IOException e) {
            throw new RuntimeException("JSON输出失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean outputToDatabase(List<DataRecord> records, String connectionId, String tableName) {
        // 数据库输出实现
        throw new RuntimeException("数据库输出功能待实现");
    }

    @Override
    public List<String> getSupportedOutputFormats() {
        return Arrays.asList("CSV", "EXCEL", "JSON", "DATABASE");
    }
}
