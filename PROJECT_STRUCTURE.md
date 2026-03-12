# 数据处理中间件系统 - 项目结构设计

## 一、项目整体架构

```
data-processing-middleware/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── middleware/
│   │   │           └── org/
│   │   │               ├── Application.java                    # Spring Boot 启动类
│   │   │               ├── config/                             # 配置类
│   │   │               │   ├── WebConfig.java                  # Web配置
│   │   │               │   └── FileStorageConfig.java          # 文件存储配置
│   │   │               ├── controller/                         # 控制器层
│   │   │               │   ├── TaskController.java             # 任务管理接口
│   │   │               │   ├── FileController.java             # 文件上传接口
│   │   │               │   ├── DataController.java             # 数据处理接口
│   │   │               │   └── LogController.java              # 日志查询接口
│   │   │               ├── service/                            # 服务接口层
│   │   │               │   ├── ITaskFlowControlService.java    # 任务流程控制
│   │   │               │   ├── IFilePreprocessService.java     # 文件预处理
│   │   │               │   ├── IDataParseService.java          # 数据解析
│   │   │               │   ├── IDataCleanService.java          # 数据清洗
│   │   │               │   ├── IDataOutputService.java         # 数据输出
│   │   │               │   └── ILogService.java                # 日志服务
│   │   │               ├── service/impl/                       # 服务实现层
│   │   │               │   ├── TaskFlowControlServiceImpl.java
│   │   │               │   ├── FilePreprocessServiceImpl.java
│   │   │               │   ├── DataParseServiceImpl.java
│   │   │               │   ├── DataCleanServiceImpl.java
│   │   │               │   ├── DataOutputServiceImpl.java
│   │   │               │   └── LogServiceImpl.java
│   │   │               ├── parser/                             # 数据解析器
│   │   │               │   ├── IDataParser.java                # 解析器接口
│   │   │               │   ├── CsvParser.java                  # CSV解析器
│   │   │               │   ├── ExcelParser.java                # Excel解析器
│   │   │               │   ├── JsonParser.java                 # JSON解析器
│   │   │               │   └── ParserFactory.java              # 解析器工厂
│   │   │               ├── cleaner/                            # 数据清洗器
│   │   │               │   ├── IDataCleaner.java               # 清洗器接口
│   │   │               │   ├── MissingValueCleaner.java        # 缺失值处理
│   │   │               │   ├── FormatValidator.java            # 格式校验
│   │   │               │   ├── DataNormalizer.java             # 数据标准化
│   │   │               │   └── CleanerChain.java               # 清洗链
│   │   │               ├── exporter/                           # 数据导出器
│   │   │               │   ├── IDataExporter.java              # 导出器接口
│   │   │               │   ├── CsvExporter.java                # CSV导出
│   │   │               │   ├── ExcelExporter.java              # Excel导出
│   │   │               │   ├── JsonExporter.java               # JSON导出
│   │   │               │   └── ExporterFactory.java            # 导出器工厂
│   │   │               ├── model/                              # 数据模型
│   │   │               │   ├── TaskContext.java                # 任务上下文
│   │   │               │   ├── TaskResult.java                 # 任务结果
│   │   │               │   ├── TaskStatus.java                 # 任务状态枚举
│   │   │               │   ├── DataRecord.java                 # 数据记录
│   │   │               │   ├── ProcessedData.java              # 处理后数据
│   │   │               │   ├── FileMetadata.java               # 文件元数据
│   │   │               │   ├── ValidationResult.java           # 校验结果
│   │   │               │   ├── CleanRule.java                  # 清洗规则
│   │   │               │   └── LogEntry.java                   # 日志条目
│   │   │               ├── exception/                          # 异常处理
│   │   │               │   ├── FileProcessException.java       # 文件处理异常
│   │   │               │   ├── DataParseException.java         # 数据解析异常
│   │   │               │   ├── DataCleanException.java         # 数据清洗异常
│   │   │               │   └── GlobalExceptionHandler.java     # 全局异常处理
│   │   │               └── util/                               # 工具类
│   │   │                   ├── FileUtil.java                   # 文件工具
│   │   │                   ├── DateUtil.java                   # 日期工具
│   │   │                   └── ValidationUtil.java             # 校验工具
│   │   └── resources/
│   │       ├── application.properties                          # 应用配置
│   │       ├── log4j2.xml                                      # 日志配置
│   │       └── static/                                         # 静态资源
│   └── test/
│       └── java/
│           └── com/
│               └── middleware/
│                   └── org/
│                       ├── service/                            # 服务测试
│                       └── parser/                             # 解析器测试
├── pom.xml                                                     # Maven配置
└── README.md                                                   # 项目说明
```

## 二、核心模块设计

### 1. 任务启动与流程控制模块
- **TaskController**: REST API入口
- **ITaskFlowControlService**: 任务流程控制接口
- **TaskFlowControlServiceImpl**: 流程控制实现，协调各模块执行

### 2. 文件上传与预处理模块
- **FileController**: 文件上传接口
- **IFilePreprocessService**: 文件预处理接口
- **FilePreprocessServiceImpl**: 文件类型识别、合法性检查

### 3. 数据解析与格式转换模块
- **IDataParser**: 解析器统一接口
- **CsvParser/ExcelParser/JsonParser**: 具体解析器实现
- **ParserFactory**: 根据文件类型创建解析器

### 4. 数据清洗与校验模块
- **IDataCleaner**: 清洗器接口
- **MissingValueCleaner**: 缺失值处理
- **FormatValidator**: 格式校验
- **DataNormalizer**: 数据标准化
- **CleanerChain**: 责任链模式组织清洗流程

### 5. 结果输出适配模块
- **IDataExporter**: 导出器接口
- **CsvExporter/ExcelExporter/JsonExporter**: 具体导出器
- **ExporterFactory**: 根据输出格式创建导出器

### 6. 日志与异常处理模块
- **ILogService**: 日志服务接口
- **LogServiceImpl**: 日志记录实现
- **GlobalExceptionHandler**: 统一异常处理

## 三、数据流转流程

```
用户上传文件
    ↓
FileController.uploadFile()
    ↓
TaskController.createTask()
    ↓
TaskFlowControlService.startTask()
    ↓
FilePreprocessService.preprocess()
    ↓
ParserFactory.getParser() → IDataParser.parse()
    ↓
DataCleanService.clean() → CleanerChain.execute()
    ↓
DataOutputService.export() → ExporterFactory.getExporter()
    ↓
返回处理结果
```

## 四、技术选型

- **开发语言**: Java 8+
- **开发框架**: Spring Boot 2.3.12
- **数据解析**: Apache POI (Excel), Jackson (JSON)
- **日志框架**: Log4j2
- **构建工具**: Maven
- **设计模式**: 工厂模式、策略模式、责任链模式

## 五、API接口设计

### 任务管理
- POST /api/task/create - 创建任务
- POST /api/task/start/{taskId} - 启动任务
- GET /api/task/status/{taskId} - 查询任务状态
- GET /api/task/list - 获取任务列表

### 文件管理
- POST /api/file/upload - 上传文件
- GET /api/file/download/{taskId} - 下载处理结果

### 数据处理
- POST /api/data/parse - 数据解析
- POST /api/data/clean - 数据清洗
- POST /api/data/export - 数据导出

### 日志查询
- GET /api/log/{taskId} - 查询任务日志
- GET /api/log/error - 查询错误日志