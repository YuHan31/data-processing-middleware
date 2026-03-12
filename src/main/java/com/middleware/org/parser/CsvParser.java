package com.middleware.org.parser;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV文件解析器
 */
@Component
public class CsvParser implements IDataParser {

    @Override
    public ProcessedData parse(String filePath) throws Exception {
        List<DataRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new Exception("CSV文件为空");
            }

            String[] headers = headerLine.split(",");
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                DataRecord record = new DataRecord();

                for (int i = 0; i < headers.length && i < values.length; i++) {
                    record.addField(headers[i].trim(), values[i].trim());
                }

                records.add(record);
            }
        }

        ProcessedData processedData = new ProcessedData();
        processedData.setRecords(records);
        processedData.setTotalCount(records.size());
        return processedData;
    }

    @Override
    public String getSupportedFileType() {
        return "csv";
    }
}
