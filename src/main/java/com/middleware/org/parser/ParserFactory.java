package com.middleware.org.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析器工厂
 * 根据文件类型返回对应的解析器
 */
@Component
public class ParserFactory {

    private final Map<String, IDataParser> parserMap = new HashMap<>();

    @Autowired
    public ParserFactory(List<IDataParser> parsers) {
        for (IDataParser parser : parsers) {
            parserMap.put(parser.getSupportedFileType(), parser);
        }
    }

    public IDataParser getParser(String fileType) {
        IDataParser parser = parserMap.get(fileType.toLowerCase());
        if (parser == null) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileType);
        }
        return parser;
    }

    public boolean isSupported(String fileType) {
        return parserMap.containsKey(fileType.toLowerCase());
    }
}
