package com.middleware.org.parser;

import com.middleware.org.model.DataRecord;
import com.middleware.org.model.ProcessedData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel文件解析器
 */
@Component
public class ExcelParser implements IDataParser {

    @Override
    public ProcessedData parse(String filePath) throws Exception {
        List<DataRecord> records = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new Exception("Excel文件表头为空");
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell));
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                DataRecord record = new DataRecord();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = cell != null ? getCellValue(cell) : "";
                    record.addField(headers.get(j), value);
                }

                records.add(record);
            }
        }

        ProcessedData processedData = new ProcessedData();
        processedData.setRecords(records);
        processedData.setTotalCount(records.size());
        return processedData;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    @Override
    public String getSupportedFileType() {
        return "xlsx";
    }
}
