# 数据处理中间件系统

## 项目简介

本系统是一个基于 Java Spring Boot 的数据处理中间件，用于对多种格式的数据文件进行统一处理。系统支持 CSV、Excel、JSON 等格式的数据接入、解析转换、数据清洗、结构化处理以及结果输出。

## 核心功能

- **多格式数据解析**: 支持 CSV、Excel (.xlsx)、JSON 格式
- **数据清洗**: 缺失值处理、格式校验、数据标准化
- **灵活的输出**: 支持导出为 CSV、Excel、JSON 格式
- **任务管理**: 完整的任务生命周期管理
- **日志记录**: 详细的任务执行日志和错误日志
- **REST API**: 提供标准的 RESTful API 接口

## 技术栈

- **开发语言**: Java 8
- **开发框架**: Spring Boot 2.3.12
- **数据解析**: Apache POI 5.2.3 (Excel), Jackson (JSON)
- **日志框架**: Log4j2
- **构建工具**: Maven

## 项目结构

```
src/main/java/com/middleware/org/
├── Application.java              # Spring Boot 启动类
├── config/                       # 配置类
│   ├── WebConfig.java
│   └── FileStorageConfig.java
├── controller/                   # 控制器层
│   ├── TaskController.java
│   ├── FileController.java
│   ├── DataController.java
│   └── LogController.java
├── service/                      # 服务接口层
│   ├── ITaskFlowControlService.java
│   ├── IFilePreprocessService.java
│   ├── IDataParseService.java
│   ├── IDataCleanService.java
│   ├── IDataOutputService.java
│   └── ILogService.java
├── service/impl/                 # 服务实现层
├── parser/                       # 数据解析器
│   ├── IDataParser.java
│   ├── CsvParser.java
│   ├── ExcelParser.java
│   ├── JsonParser.java
│   └── ParserFactory.java
├── cleaner/                      # 数据清洗器
│   ├── IDataCleaner.java
│   ├── MissingValueCleaner.java
│   ├── FormatValidator.java
│   ├── DataNormalizer.java
│   └── CleanerChain.java
├── exporter/                     # 数据导出器
│   ├── IDataExporter.java
│   ├── CsvExporter.java
│   ├── ExcelExporter.java
│   ├── JsonExporter.java
│   └── ExporterFactory.java
├── model/                        # 数据模型
│   ├── TaskContext.java
│   ├── TaskResult.java
│   ├── TaskStatus.java
│   ├── DataRecord.java
│   ├── ProcessedData.java
│   ├── CleanRule.java
│   ├── FileMetadata.java
│   ├── ValidationResult.java
│   └── LogEntry.java
├── exception/                    # 异常处理
│   ├── FileProcessException.java
│   ├── DataParseException.java
│   ├── DataCleanException.java
│   └── GlobalExceptionHandler.java
└── util/                         # 工具类
    ├── FileUtil.java
    ├── DateUtil.java
    └── ValidationUtil.java
```

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+

### 构建项目

```bash
mvn clean install
```

### 运行项目

```bash
mvn spring-boot:run
```

或者

```bash
java -jar target/data-processing-middleware-1.0-SNAPSHOT.jar
```

### 访问应用

启动成功后，访问:
- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/api

## API 接口

### 任务管理

- `POST /api/task/create` - 创建任务
- `POST /api/task/start/{taskId}` - 启动任务
- `GET /api/task/status/{taskId}` - 查询任务状态
- `GET /api/task/list` - 获取任务列表

### 文件管理

- `POST /api/file/upload` - 上传文件
- `GET /api/file/download/{taskId}` - 下载处理结果

### 数据处理

- `POST /api/data/parse` - 数据解析
- `POST /api/data/clean` - 数据清洗
- `POST /api/data/export` - 数据导出

### 日志查询

- `GET /api/log/{taskId}` - 查询任务日志
- `GET /api/log/error` - 查询错误日志

## 使用示例

### 1. 上传文件

```bash
curl -X POST http://localhost:8080/api/file/upload \
  -F "file=@data.csv"
```

### 2. 创建任务

```bash
curl -X POST http://localhost:8080/api/task/create \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "数据处理任务",
    "inputFilePath": "/uploads/data.csv",
    "outputFilePath": "/outputs/result.json",
    "fileType": "csv"
  }'
```

### 3. 启动任务

```bash
curl -X POST http://localhost:8080/api/task/start/{taskId}
```

### 4. 查询任务状态

```bash
curl -X GET http://localhost:8080/api/task/status/{taskId}
```

## 数据处理流程

```
用户上传文件
    ↓
文件预处理 (类型识别、合法性检查)
    ↓
数据解析 (CSV/Excel/JSON → DataRecord)
    ↓
数据清洗 (缺失值处理、格式校验、标准化)
    ↓
数据输出 (导出为指定格式)
    ↓
返回处理结果
```

## 配置说明

在 `application.properties` 中配置:

```properties
# 服务器端口
server.port=8080

# 文件上传路径
file.upload.path=./uploads

# 文件输出路径
file.output.path=./outputs

# 日志配置
logging.level.com.middleware=INFO
```

## 扩展开发

### 添加新的数据解析器

1. 实现 `IDataParser` 接口
2. 添加 `@Component` 注解
3. 实现 `parse()` 和 `getSupportedFileType()` 方法
4. Spring 会自动注册到 `ParserFactory`

### 添加新的数据清洗器

1. 实现 `IDataCleaner` 接口
2. 添加 `@Component` 注解
3. 在 `CleanerChain` 中添加执行逻辑

### 添加新的导出器

1. 实现 `IDataExporter` 接口
2. 添加 `@Component` 注解
3. 实现 `export()` 和 `getSupportedFormat()` 方法

## 设计模式

- **工厂模式**: ParserFactory, ExporterFactory
- **策略模式**: IDataParser, IDataCleaner, IDataExporter
- **责任链模式**: CleanerChain
- **依赖注入**: Spring IoC

## 文档

- [项目结构设计](PROJECT_STRUCTURE.md)
- [核心类设计](CORE_CLASSES_DESIGN.md)

## 许可证

本项目仅用于学习和研究目的。
