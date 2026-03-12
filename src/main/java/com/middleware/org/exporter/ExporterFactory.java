package com.middleware.org.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出器工厂
 * 根据输出格式返回对应的导出器
 */
@Component
public class ExporterFactory {

    private final Map<String, IDataExporter> exporterMap = new HashMap<>();

    @Autowired
    public ExporterFactory(List<IDataExporter> exporters) {
        for (IDataExporter exporter : exporters) {
            exporterMap.put(exporter.getSupportedFormat(), exporter);
        }
    }

    public IDataExporter getExporter(String format) {
        IDataExporter exporter = exporterMap.get(format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("不支持的导出格式: " + format);
        }
        return exporter;
    }

    public boolean isSupported(String format) {
        return exporterMap.containsKey(format.toLowerCase());
    }
}
