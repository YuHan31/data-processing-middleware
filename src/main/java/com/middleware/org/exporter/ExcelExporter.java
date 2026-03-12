package com.middleware.org.exporter;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

/**
 * Excel导出器
 */
@Component
public class ExcelExporter implements IDataExporter {

    @Override
    public void export(ProcessedData data, String outputPath) throws Exception {
        List<DataRecord> records = data.getRecords();
        if (records.isEmpty()) {
            throw new Exception("没有数据可导出");
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(outputPath)) {

            Sheet sheet = workbook.createSheet("Data");
            Set<String> headers = records.get(0).getFields().keySet();

            Row headerRow = sheet.createRow(0);
            int colIndex = 0;
            for (String header : headers) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(header);
            }

            int rowIndex = 1;
            for (DataRecord record : records) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (String header : headers) {
                    Cell cell = row.createCell(colIndex++);
                    Object value = record.getField(header);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            workbook.write(fos);
        }
    }

    @Override
    public String getSupportedFormat() {
        return "xlsx";
    }
}
