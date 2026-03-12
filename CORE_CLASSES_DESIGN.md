# 数据处理中间件 - 核心类设计文档

## 一、数据模型层 (model)

### 1. TaskContext.java
**功能**: 任务上下文，包含任务执行所需的所有信息

**字段**:
```java
- taskId: String                    // 任务ID
- taskName: String                  // 任务名称
- inputFilePath: String             // 输入文件路径
- outputFilePath: String            // 输出文件路径
- fileType: String                  // 文件类型（csv/xlsx/json）
- status: TaskStatus                // 任务状态
- processedData: ProcessedData      // 处理后的数据
- statistics: DataStatistics        // 统计信息
- parameters: Map<String, Object>   // 扩展参数
```

**方法**:
```java
+ getTaskId(): String
+ setTaskId(taskId: String): void
+ getStatus(): TaskStatus
+ setStatus(status: TaskStatus): void
+ getProcessedData(): ProcessedData
+ setProcessedData(data: ProcessedData): void
+ addParameter(key: String, value: Object): void
+ getParameter(key: String): Object
```

### 2. TaskResult.java
**功能**: 任务执行结果

**字段**:
```java
- taskId: String                    // 任务ID
- taskName: String                  // 任务名称
- status: String                    // 任务状态
- message: String                   // 结果消息
- startTime: Long                   // 开始时间
- endTime: Long                     // 结束时间
- inputFilePath: String             // 输入文件路径
- outputFilePath: String            // 输出文件路径
- processedRecords: Integer         // 处理记录数
- errorRecords: Integer             // 错误记录数
```

### 3. TaskStatus.java (枚举)
**功能**: 任务状态枚举

**枚举值**:
```java
UPLOADED("已上传")       // 文件已上传
PARSING("解析中")        // 正在解析数据
CLEANING("清洗中")       // 正在清洗数据
NORMALIZING("标准化中")  // 正在标准化数据
EXPORTING("导出中")      // 正在导出数据
FINISHED("已完成")       // 任务完成
FAILED("失败")           // 任务失败
```

### 4. DataRecord.java
**功能**: 数据记录，表示一行数据

**字段**:
```java
- recordId: Long                    // 记录ID
- fields: Map<String, Object>       // 字段映射
- sourceType: String                // 数据源类型
- timestamp: Long                   // 时间戳
- valid: boolean                    // 是否有效
```

**方法**:
```java
+ addField(key: String, value: Object): void
+ getField(key: String): Object
+ isValid(): boolean
+ setValid(valid: boolean): void
+ getData(): Map<String, Object>
```

### 5. ProcessedData.java
**功能**: 处理后的数据集合

**字段**:
```java
- records: List<DataRecord>         // 数据记录列表
- totalCount: int                   // 总记录数
- validCount: int                   // 有效记录数
- invalidCount: int                 // 无效记录数
```

**方法**:
```java
+ getRecords(): List<DataRecord>
+ setRecords(records: List<DataRecord>): void
+ addRecord(record: DataRecord): void
+ getTotalCount(): int
+ setTotalCount(count: int): void
```

### 6. DataStatistics.java
**功能**: 数据统计信息

**字段**:
```java
- taskId: String                    // 任务ID
- totalRecords: long                // 总记录数
- validRecords: long                // 有效记录数
- invalidRecords: long              // 无效记录数
- missingValues: long               // 缺失值数量
- processingTimeMs: long            // 处理时间（毫秒）
- startTime: String                 // 开始时间
- endTime: String                   // 结束时间
```

### 7. TaskProgress.java
**功能**: 任务进度信息

**字段**:
```java
- taskId: String                    // 任务ID
- status: TaskStatus                // 任务状态
- percentage: int                   // 进度百分比（0-100）
- currentStage: String              // 当前阶段
- message: String                   // 进度消息
- updateTime: long                  // 更新时间
```

### 8. CleanRule.java
**功能**: 数据清洗规则配置

**字段**:
```java
- handleMissingValue: boolean               // 是否处理缺失值
- missingValueStrategy: String              // 缺失值处理策略
- fieldTypeMap: Map<String, String>         // 字段类型映射
- fieldFormatMap: Map<String, String>       // 字段格式映射
- removeInvalidRecords: boolean             // 是否删除无效记录
- normalizeData: boolean                    // 是否标准化数据
```

### 9. FileMetadata.java
**功能**: 文件元数据

**字段**:
```java
- fileName: String                  // 文件名
- fileSize: long                    // 文件大小
- fileType: String                  // 文件类型
- uploadTime: Date                  // 上传时间
```

### 10. ValidationResult.java
**功能**: 数据校验结果

**字段**:
```java
- valid: boolean                    // 是否有效
- totalRecords: int                 // 总记录数
- validRecords: int                 // 有效记录数
- invalidRecords: int               // 无效记录数
- warnings: List<String>            // 警告消息列表
```

### 11. LogEntry.java
**功能**: 日志条目

**字段**:
```java
- level: String                     // 日志级别（INFO/WARN/ERROR）
- message: String                   // 日志消息
- timestamp: long                   // 时间戳
- threadName: String                // 线程名称
- exceptionMessage: String          // 异常消息
- stackTrace: String                // 堆栈跟踪
```

## 二、服务接口层 (service)

### 1. ITaskFlowControlService.java
**功能**: 任务流程控制服务

**方法**:
```java
+ createTask(taskContext: TaskContext): String
    // 创建任务，返回任务ID

+ startTask(taskId: String): void
    // 启动任务（异步执行）

+ getTaskStatus(taskId: String): TaskStatus
    // 查询任务状态

+ listAllTasks(): List<TaskResult>
    // 获取所有任务列表

+ stopTask(taskId: String): boolean
    // 停止任务
```

### 2. IDataParseService.java
**功能**: 数据解析服务

**方法**:
```java
+ parse(taskContext: TaskContext): void
    // 解析数据文件，结果存入taskContext
```

### 3. IDataCleanService.java
**功能**: 数据清洗服务

**方法**:
```java
+ clean(taskContext: TaskContext): void
    // 清洗数据

+ normalize(taskContext: TaskContext): void
    // 标准化数据
```

### 4. IDataOutputService.java
**功能**: 数据输出服务

**方法**:
```java
+ export(taskContext: TaskContext): void
    // 导出数据到指定格式
```

### 5. ILogService.java
**功能**: 日志服务

**方法**:
```java
+ info(taskId: String, message: String): void
    // 记录信息日志

+ warn(taskId: String, message: String): void
    // 记录警告日志

+ error(taskId: String, message: String): void
    // 记录错误日志

+ queryLogs(taskId: String): List<LogEntry>
    // 查询任务日志
```

## 三、数据解析器 (parser)

### 1. IDataParser.java (接口)
**功能**: 数据解析器统一接口

**方法**:
```java
+ parse(filePath: String): ProcessedData throws Exception
    // 解析文件，返回处理后的数据

+ getSupportedFileType(): String
    // 获取支持的文件类型
```

### 2. CsvParser.java
**功能**: CSV文件解析器

**实现细节**:
- 使用BufferedReader逐行读取
- 第一行作为表头
- 将每行数据转换为DataRecord
- 返回ProcessedData对象

**关键代码**:
```java
@Component
public class CsvParser implements IDataParser {
    @Override
    public ProcessedData parse(String filePath) throws Exception {
        // 读取CSV文件
        // 解析表头和数据行
        // 转换为DataRecord列表
        // 返回ProcessedData
    }

    @Override
    public String getSupportedFileType() {
        return "csv";
    }
}
```

### 3. ExcelParser.java
**功能**: Excel文件解析器

**实现细节**:
- 使用Apache POI读取Excel
- 支持.xlsx格式
- 处理多种单元格类型（字符串、数字、日期、布尔值）
- 第一行作为表头

**关键代码**:
```java
@Component
public class ExcelParser implements IDataParser {
    @Override
    public ProcessedData parse(String filePath) throws Exception {
        // 使用XSSFWorkbook读取Excel
        // 获取第一个Sheet
        // 解析表头和数据行
        // 返回ProcessedData
    }

    @Override
    public String getSupportedFileType() {
        return "xlsx";
    }
}
```

### 4. JsonParser.java
**功能**: JSON文件解析器

**实现细节**:
- 使用Jackson ObjectMapper解析JSON
- 支持数组格式的JSON数据
- 将JSON对象转换为DataRecord

**关键代码**:
```java
@Component
public class JsonParser implements IDataParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProcessedData parse(String filePath) throws Exception {
        // 使用ObjectMapper读取JSON
        // 转换为DataRecord列表
        // 返回ProcessedData
    }

    @Override
    public String getSupportedFileType() {
        return "json";
    }
}
```

### 5. ParserFactory.java
**功能**: 解析器工厂

**实现细节**:
- Spring自动注入所有IDataParser实现
- 根据文件类型返回对应的解析器
- 支持动态注册新的解析器

**关键代码**:
```java
@Component
public class ParserFactory {
    private final Map<String, IDataParser> parserMap;

    @Autowired
    public ParserFactory(List<IDataParser> parsers) {
        // 自动注册所有解析器
    }

    public IDataParser getParser(String fileType) {
        // 根据文件类型返回解析器
    }
}
```

## 四、数据清洗器 (cleaner)

### 1. MissingValueCleaner.java
**功能**: 缺失值处理器

**处理策略**:
- 检测null值和空字符串
- 填充为空字符串
- 标记记录为无效

**关键代码**:
```java
@Component
public class MissingValueCleaner {
    public void clean(ProcessedData processedData) {
        // 遍历所有记录
        // 检查缺失值
        // 填充并标记
    }
}
```

### 2. FormatValidator.java
**功能**: 格式校验器

**校验规则**:
- 检查空值
- 标记无效记录

**关键代码**:
```java
@Component
public class FormatValidator {
    public void clean(ProcessedData processedData) {
        // 遍历所有记录
        // 校验格式
        // 标记无效记录
    }
}
```

### 3. DataNormalizer.java
**功能**: 数据标准化处理器

**标准化规则**:
- 去除首尾空格
- 去除多余空格
- 统一字符串格式

**关键代码**:
```java
@Component
public class DataNormalizer {
    public void normalize(ProcessedData processedData) {
        // 遍历所有记录
        // 标准化字符串字段
    }
}
```

### 4. CleanerChain.java
**功能**: 清洗链（责任链模式）

**执行流程**:
1. MissingValueCleaner - 处理缺失值
2. FormatValidator - 格式校验
3. DataNormalizer - 数据标准化

**关键代码**:
```java
@Component
public class CleanerChain {
    @Autowired
    private MissingValueCleaner missingValueCleaner;

    @Autowired
    private FormatValidator formatValidator;

    @Autowired
    private DataNormalizer dataNormalizer;

    public void clean(ProcessedData processedData) {
        missingValueCleaner.clean(processedData);
        formatValidator.clean(processedData);
    }

    public void normalize(ProcessedData processedData) {
        dataNormalizer.normalize(processedData);
    }
}
```

## 五、数据导出器 (exporter)

### 1. IDataExporter.java (接口)
**功能**: 数据导出器统一接口

**方法**:
```java
+ export(data: ProcessedData, outputPath: String): void throws Exception
    // 导出数据到指定路径

+ getSupportedFormat(): String
    // 获取支持的格式
```

### 2. CsvExporter.java
**功能**: CSV导出器

**实现细节**:
- 使用BufferedWriter写入CSV
- 第一行写入表头
- 逐行写入数据

### 3. ExcelExporter.java
**功能**: Excel导出器

**实现细节**:
- 使用Apache POI生成Excel
- 创建Sheet和Row
- 写入表头和数据

### 4. JsonExporter.java
**功能**: JSON导出器

**实现细节**:
- 使用Jackson ObjectMapper
- 格式化输出JSON
- 美化打印

### 5. ExporterFactory.java
**功能**: 导出器工厂

**实现细节**:
- Spring自动注入所有IDataExporter实现
- 根据输出格式返回对应的导出器

## 六、任务执行模块 (task)

### 1. TaskExecutor.java
**功能**: 异步任务执行器

**执行流程**:
```java
@Component
public class TaskExecutor {
    @Async("taskExecutor")
    public void executeTask(TaskContext taskContext) {
        // 1. 数据解析阶段
        progressService.updateProgress(taskId, PARSING);
        dataParseService.parse(taskContext);

        // 2. 数据清洗阶段
        progressService.updateProgress(taskId, CLEANING);
        dataCleanService.clean(taskContext);

        // 3. 数据标准化阶段
        progressService.updateProgress(taskId, NORMALIZING);
        dataCleanService.normalize(taskContext);

        // 4. 数据统计阶段
        statisticsService.generateStatistics(taskContext);

        // 5. 数据导出阶段
        progressService.updateProgress(taskId, EXPORTING);
        dataOutputService.export(taskContext);

        // 6. 任务完成
        progressService.updateProgress(taskId, FINISHED);
    }
}
```

### 2. TaskThreadPoolConfig.java
**功能**: 线程池配置

**配置参数**:
```java
@Configuration
public class TaskThreadPoolConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);        // 核心线程数
        executor.setMaxPoolSize(10);        // 最大线程数
        executor.setQueueCapacity(100);     // 队列容量
        executor.setKeepAliveSeconds(60);   // 线程空闲时间
        executor.setThreadNamePrefix("task-executor-");
        return executor;
    }
}
```

## 七、其他核心模块

### 1. StatisticsService.java
**功能**: 数据统计服务

**统计内容**:
- 总记录数
- 有效记录数
- 无效记录数
- 缺失值数量
- 处理时间

### 2. ProgressService.java
**功能**: 进度监控服务

**进度阶段**:
- UPLOADED (10%)
- PARSING (30%)
- CLEANING (50%)
- NORMALIZING (70%)
- EXPORTING (90%)
- FINISHED (100%)

### 3. TaskRepository.java
**功能**: 任务数据仓库

**存储方式**:
- 使用ConcurrentHashMap存储任务
- 线程安全的读写操作

### 4. LogServiceImpl.java
**功能**: 日志服务实现

**日志级别**:
- INFO: 信息日志
- WARN: 警告日志
- ERROR: 错误日志

## 八、控制器层 (controller)

### 1. TaskController.java
**REST API**:
```java
@RestController
@RequestMapping("/api/task")
public class TaskController {
    POST /api/task/create      // 创建任务
    POST /api/task/start/{id}  // 启动任务
    GET  /api/task/status/{id} // 查询状态
    GET  /api/task/list        // 任务列表
    POST /api/task/stop/{id}   // 停止任务
}
```

### 2. FileController.java
**REST API**:
```java
@RestController
@RequestMapping("/api/file")
public class FileController {
    POST /api/file/upload         // 上传文件
    GET  /api/file/download/{id}  // 下载结果
}
```

### 3. LogController.java
**REST API**:
```java
@RestController
@RequestMapping("/api/log")
public class LogController {
    GET /api/log/{taskId}  // 查询任务日志
}
```

## 九、完整数据处理流程

```
1. 用户上传文件或创建任务
   ↓
2. TaskController.createTask()
   - 生成任务ID
   - 初始化TaskContext
   - 保存到TaskRepository
   ↓
3. TaskController.startTask()
   - 调用TaskExecutor.executeTask()
   ↓
4. TaskExecutor异步执行
   ├─ 阶段1: 数据解析 (PARSING)
   │  └─ ParserFactory → IDataParser.parse()
   ├─ 阶段2: 数据清洗 (CLEANING)
   │  └─ CleanerChain.clean()
   ├─ 阶段3: 数据标准化 (NORMALIZING)
   │  └─ CleanerChain.normalize()
   ├─ 阶段4: 数据统计
   │  └─ StatisticsService.generateStatistics()
   └─ 阶段5: 数据导出 (EXPORTING)
      └─ ExporterFactory → IDataExporter.export()
   ↓
5. 更新任务状态为FINISHED
   ↓
6. 返回处理结果
```

## 十、设计模式总结

1. **工厂模式**: ParserFactory, ExporterFactory
2. **策略模式**: IDataParser, IDataExporter
3. **责任链模式**: CleanerChain
4. **依赖注入**: Spring IoC容器管理所有Bean
5. **异步模式**: @Async注解实现异步任务执行
