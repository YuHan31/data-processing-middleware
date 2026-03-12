package com.middleware.org.exporter;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CSV导出器
 */
@Component
public class CsvExporter implements IDataExporter {

    @Override
    public void export(ProcessedData data, String outputPath) throws Exception {
        List<DataRecord> records = data.getRecords();
        if (records.isEmpty()) {
            throw new Exception("没有数据可导出");
        }

        Set<String> headers = records.get(0).getFields().keySet();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write(String.join(",", headers));
            writer.newLine();

            for (DataRecord record : records) {
                List<String> values = headers.stream()
                    .map(header -> {
                        Object value = record.getField(header);
                        return value != null ? value.toString() : "";
                    })
                    .collect(Collectors.toList());

                writer.write(String.join(",", values));
                writer.newLine();
            }
        }
    }

    @Override
    public String getSupportedFormat() {
        return "csv";
    }
}
