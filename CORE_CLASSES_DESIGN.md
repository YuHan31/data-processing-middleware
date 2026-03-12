# 数据处理中间件 - 核心类设计文档

## 一、数据模型层 (model)

### 1. TaskContext.java
**功能**: 任务上下文，包含任务执行所需的所有信息
```java
- taskId: String              // 任务ID
- taskName: String            // 任务名称
- inputFilePath: String       // 输入文件路径
- outputFilePath: String      // 输出文件路径
- fileType: String            // 文件类型
- parameters: Map             // 扩展参数
```

### 2. TaskResult.java
**功能**: 任务执行结果
```java
- taskId: String              // 任务ID
- status: TaskStatus          // 任务状态
- startTime: Date             // 开始时间
- endTime: Date               // 结束时间
- message: String             // 结果消息
- processedData: ProcessedData // 处理后的数据
```

### 3. TaskStatus.java (枚举)
**功能**: 任务状态枚举
```java
- WAITING    // 等待中
- RUNNING    // 运行中
- SUCCESS    // 成功
- FAILED     // 失败
```

### 4. DataRecord.java
**功能**: 数据记录，表示一行数据
```java
- fields: Map<String, Object>  // 字段映射
+ addField(key, value)         // 添加字段
+ getField(key)                // 获取字段
```

### 5. ProcessedData.java
**功能**: 处理后的数据集合
```java
- records: List<DataRecord>    // 数据记录列表
- totalCount: int              // 总记录数
- validCount: int              // 有效记录数
- invalidCount: int            // 无效记录数
```

### 6. CleanRule.java
**功能**: 数据清洗规则配置
```java
- handleMissingValue: boolean          // 是否处理缺失值
- missingValueStrategy: String         // 缺失值处理策略
- fieldTypeMap: Map<String, String>    // 字段类型映射
- fieldFormatMap: Map<String, String>  // 字段格式映射
- removeInvalidRecords: boolean        // 是否删除无效记录
- normalizeData: boolean               // 是否标准化数据
```

### 7. FileMetadata.java
**功能**: 文件元数据
```java
- fileName: String            // 文件名
- fileSize: long              // 文件大小
- fileType: String            // 文件类型
- uploadTime: Date            // 上传时间
```

### 8. ValidationResult.java
**功能**: 数据校验结果
```java
- isValid: boolean            // 是否有效
- errorMessages: List<String> // 错误消息列表
```

### 9. LogEntry.java
**功能**: 日志条目
```java
- taskId: String              // 任务ID
- level: String               // 日志级别
- message: String             // 日志消息
- timestamp: Date             // 时间戳
```

## 二、服务接口层 (service)

### 1. ITaskFlowControlService.java
**功能**: 任务流程控制服务
```java
+ startTask(TaskContext): String           // 启动任务
+ stopTask(taskId): boolean                // 停止任务
+ getTaskStatus(taskId): TaskResult        // 查询任务状态
+ listAllTasks(): List<TaskResult>         // 获取所有任务
```

### 2. IFilePreprocessService.java
**功能**: 文件预处理服务
```java
+ uploadFile(file): FileMetadata           // 上传文件
+ validateFile(filePath): ValidationResult // 验证文件
+ identifyFileType(filePath): String       // 识别文件类型
```

### 3. IDataParseService.java
**功能**: 数据解析服务
```java
+ parse(filePath, fileType): List<DataRecord>  // 解析文件
```

### 4. IDataCleanService.java
**功能**: 数据清洗服务
```java
+ clean(records, rule): List<DataRecord>   // 清洗数据
```

### 5. IDataOutputService.java
**功能**: 数据输出服务
```java
+ export(data, outputPath, format): void   // 导出数据
```

### 6. ILogService.java
**功能**: 日志服务
```java
+ log(taskId, level, message): void        // 记录日志
+ getTaskLogs(taskId): List<LogEntry>      // 获取任务日志
+ getErrorLogs(): List<LogEntry>           // 获取错误日志
```

## 三、数据解析器 (parser)

### 1. IDataParser.java (接口)
**功能**: 数据解析器统一接口
```java
+ parse(filePath): List<DataRecord>        // 解析文件
+ getSupportedFileType(): String           // 获取支持的文件类型
```

### 2. CsvParser.java
**功能**: CSV文件解析器
- 读取CSV文件
- 解析表头和数据行
- 转换为DataRecord列表

### 3. ExcelParser.java
**功能**: Excel文件解析器
- 使用Apache POI读取Excel
- 支持.xlsx格式
- 处理多种单元格类型

### 4. JsonParser.java
**功能**: JSON文件解析器
- 使用Jackson解析JSON
- 支持数组格式的JSON数据
- 转换为统一的DataRecord格式

### 5. ParserFactory.java
**功能**: 解析器工厂
- 根据文件类型返回对应的解析器
- 支持动态注册新的解析器

## 四、数据清洗器 (cleaner)

### 1. IDataCleaner.java (接口)
**功能**: 数据清洗器统一接口
```java
+ clean(records, rule): List<DataRecord>   // 清洗数据
+ getCleanerName(): String                 // 获取清洗器名称
```

### 2. MissingValueCleaner.java
**功能**: 缺失值处理器
- 检测空值和null
- 支持多种填充策略：默认值、null、0

### 3. FormatValidator.java
**功能**: 格式校验器
- 校验字段类型（数字、邮箱等）
- 标记或删除无效记录

### 4. DataNormalizer.java
**功能**: 数据标准化处理器
- 去除多余空格
- 统一字符串格式

### 5. CleanerChain.java
**功能**: 清洗链
- 按顺序执行多个清洗器
- 责任链模式实现

## 五、数据导出器 (exporter)

### 1. IDataExporter.java (接口)
**功能**: 数据导出器统一接口
```java
+ export(data, outputPath): void           // 导出数据
+ getSupportedFormat(): String             // 获取支持的格式
```

### 2. CsvExporter.java
**功能**: CSV导出器
- 将ProcessedData导出为CSV格式

### 3. ExcelExporter.java
**功能**: Excel导出器
- 使用Apache POI生成Excel文件

### 4. JsonExporter.java
**功能**: JSON导出器
- 使用Jackson生成JSON文件

### 5. ExporterFactory.java
**功能**: 导出器工厂
- 根据输出格式返回对应的导出器

## 六、控制器层 (controller)

### 1. TaskController.java
**功能**: 任务管理接口
```java
POST   /api/task/create      // 创建任务
POST   /api/task/start/{id}  // 启动任务
GET    /api/task/status/{id} // 查询状态
GET    /api/task/list        // 任务列表
```

### 2. FileController.java
**功能**: 文件管理接口
```java
POST   /api/file/upload      // 上传文件
GET    /api/file/download/{id} // 下载结果
```

### 3. DataController.java
**功能**: 数据处理接口
```java
POST   /api/data/parse       // 数据解析
POST   /api/data/clean       // 数据清洗
POST   /api/data/export      // 数据导出
```

### 4. LogController.java
**功能**: 日志查询接口
```java
GET    /api/log/{taskId}     // 查询任务日志
GET    /api/log/error        // 查询错误日志
```

## 七、异常处理 (exception)

### 1. FileProcessException.java
**功能**: 文件处理异常

### 2. DataParseException.java
**功能**: 数据解析异常

### 3. DataCleanException.java
**功能**: 数据清洗异常

### 4. GlobalExceptionHandler.java
**功能**: 全局异常处理器
- 统一处理各类异常
- 返回标准错误响应

## 八、工具类 (util)

### 1. FileUtil.java
**功能**: 文件工具类
```java
+ getFileExtension(fileName): String       // 获取文件扩展名
+ isFileExists(filePath): boolean          // 检查文件是否存在
+ getFileSize(filePath): long              // 获取文件大小
```

### 2. DateUtil.java
**功能**: 日期工具类
```java
+ formatNow(): String                      // 格式化当前时间
+ format(date, pattern): String            // 格式化日期
```

### 3. ValidationUtil.java
**功能**: 校验工具类
```java
+ isEmail(email): boolean                  // 校验邮箱
+ isNumber(str): boolean                   // 校验数字
+ isEmpty(str): boolean                    // 检查空字符串
```

## 九、配置类 (config)

### 1. WebConfig.java
**功能**: Web配置
- 配置CORS跨域
- 配置拦截器

### 2. FileStorageConfig.java
**功能**: 文件存储配置
- 配置上传路径
- 配置输出路径
- 自动创建目录

## 十、核心流程

### 完整数据处理流程
```
1. 用户上传文件 (FileController)
   ↓
2. 文件预处理 (FilePreprocessService)
   - 文件类型识别
   - 文件合法性检查
   ↓
3. 创建任务 (TaskController)
   - 生成任务ID
   - 初始化任务上下文
   ↓
4. 启动任务 (TaskFlowControlService)
   ↓
5. 数据解析 (DataParseService)
   - ParserFactory获取解析器
   - 解析为DataRecord列表
   ↓
6. 数据清洗 (DataCleanService)
   - CleanerChain执行清洗
   - 缺失值处理
   - 格式校验
   - 数据标准化
   ↓
7. 数据输出 (DataOutputService)
   - ExporterFactory获取导出器
   - 导出为指定格式
   ↓
8. 返回结果
   - 更新任务状态
   - 记录日志
```

## 十一、设计模式应用

1. **工厂模式**: ParserFactory, ExporterFactory
2. **策略模式**: IDataParser, IDataCleaner, IDataExporter
3. **责任链模式**: CleanerChain
4. **单例模式**: Spring Bean管理
