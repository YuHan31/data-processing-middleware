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
│   │   │               ├── Application.java                    # Spring Boot启动类
│   │   │               │
│   │   │               ├── controller/                         # REST API控制器层
│   │   │               │   ├── TaskController.java             # 任务管理接口
│   │   │               │   ├── FileController.java             # 文件上传接口
│   │   │               │   └── LogController.java              # 日志查询接口
│   │   │               │
│   │   │               ├── service/                            # 服务接口层
│   │   │               │   ├── ITaskFlowControlService.java    # 任务流程控制
│   │   │               │   ├── IDataParseService.java          # 数据解析
│   │   │               │   ├── IDataCleanService.java          # 数据清洗
│   │   │               │   ├── IDataOutputService.java         # 数据输出
│   │   │               │   └── ILogService.java                # 日志服务
│   │   │               │
│   │   │               ├── service/impl/                       # 服务实现层
│   │   │               │   ├── TaskFlowControlServiceImpl.java # 任务流程控制实现
│   │   │               │   ├── DataParseServiceImpl.java       # 数据解析实现
│   │   │               │   ├── DataCleanServiceImpl.java       # 数据清洗实现
│   │   │               │   ├── DataOutputServiceImpl.java      # 数据输出实现
│   │   │               │   └── LogServiceImpl.java             # 日志服务实现
│   │   │               │
│   │   │               ├── task/                               # 任务执行模块
│   │   │               │   ├── TaskExecutor.java               # 异步任务执行器
│   │   │               │   └── TaskThreadPoolConfig.java       # 线程池配置
│   │   │               │
│   │   │               ├── parser/                             # 数据解析器模块
│   │   │               │   ├── IDataParser.java                # 解析器接口
│   │   │               │   ├── CsvParser.java                  # CSV解析器
│   │   │               │   ├── ExcelParser.java                # Excel解析器
│   │   │               │   ├── JsonParser.java                 # JSON解析器
│   │   │               │   └── ParserFactory.java              # 解析器工厂
│   │   │               │
│   │   │               ├── cleaner/                            # 数据清洗器模块
│   │   │               │   ├── MissingValueCleaner.java        # 缺失值处理器
│   │   │               │   ├── FormatValidator.java            # 格式校验器
│   │   │               │   ├── DataNormalizer.java             # 数据标准化器
│   │   │               │   └── CleanerChain.java               # 清洗链
│   │   │               │
│   │   │               ├── exporter/                           # 数据导出器模块
│   │   │               │   ├── IDataExporter.java              # 导出器接口
│   │   │               │   ├── CsvExporter.java                # CSV导出器
│   │   │               │   ├── ExcelExporter.java              # Excel导出器
│   │   │               │   ├── JsonExporter.java               # JSON导出器
│   │   │               │   └── ExporterFactory.java            # 导出器工厂
│   │   │               │
│   │   │               ├── statistics/                         # 数据统计模块
│   │   │               │   ├── DataStatistics.java             # 统计数据模型
│   │   │               │   └── StatisticsService.java          # 统计服务
│   │   │               │
│   │   │               ├── progress/                           # 进度监控模块
│   │   │               │   ├── TaskProgress.java               # 进度数据模型
│   │   │               │   └── ProgressService.java            # 进度服务
│   │   │               │
│   │   │               ├── repository/                         # 数据仓库模块
│   │   │               │   └── TaskRepository.java             # 任务数据仓库
│   │   │               │
│   │   │               ├── model/                              # 数据模型层
│   │   │               │   ├── TaskContext.java                # 任务上下文
│   │   │               │   ├── TaskResult.java                 # 任务结果
│   │   │               │   ├── TaskStatus.java                 # 任务状态枚举
│   │   │               │   ├── DataRecord.java                 # 数据记录
│   │   │               │   ├── ProcessedData.java              # 处理后数据
│   │   │               │   ├── FileMetadata.java               # 文件元数据
│   │   │               │   ├── ValidationResult.java           # 校验结果
│   │   │               │   ├── CleanRule.java                  # 清洗规则
│   │   │               │   └── LogEntry.java                   # 日志条目
│   │   │               │
│   │   │               ├── exception/                          # 异常处理模块
│   │   │               │   ├── FileProcessException.java       # 文件处理异常
│   │   │               │   ├── DataParseException.java         # 数据解析异常
│   │   │               │   ├── DataCleanException.java         # 数据清洗异常
│   │   │               │   └── GlobalExceptionHandler.java     # 全局异常处理器
│   │   │               │
│   │   │               ├── util/                               # 工具类模块
│   │   │               │   ├── FileUtil.java                   # 文件工具
│   │   │               │   ├── DateUtil.java                   # 日期工具
│   │   │               │   └── ValidationUtil.java             # 校验工具
│   │   │               │
│   │   │               └── config/                             # 配置类模块
│   │   │                   ├── WebConfig.java                  # Web配置
│   │   │                   └── FileStorageConfig.java          # 文件存储配置
│   │   │
│   │   └── resources/
│   │       ├── application.properties                          # 应用配置文件
│   │       └── static/                                         # 静态资源目录
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── middleware/
│                   └── org/
│                       ├── service/                            # 服务测试
│                       ├── parser/                             # 解析器测试
│                       └── cleaner/                            # 清洗器测试
│
├── uploads/                                                    # 文件上传目录
├── output/                                                     # 文件输出目录
├── test-data.csv                                               # 测试数据文件
├── start.sh                                                    # 启动脚本
├── test-api.sh                                                 # API测试脚本
├── pom.xml                                                     # Maven配置文件
├── README.md                                                   # 项目说明文档
├── PROJECT_STRUCTURE.md                                        # 项目结构文档
├── CORE_CLASSES_DESIGN.md                                      # 核心类设计文档
└── 使用指南.md                                                  # 使用指南文档
```

## 二、核心模块详细设计

### 1. 控制器层 (controller)

#### TaskController
- **功能**: 任务管理REST API
- **接口**:
  - POST /api/task/create - 创建任务
  - POST /api/task/start/{taskId} - 启动任务
  - GET /api/task/status/{taskId} - 查询任务状态
  - GET /api/task/list - 获取任务列表
  - POST /api/task/stop/{taskId} - 停止任务

#### FileController
- **功能**: 文件管理REST API
- **接口**:
  - POST /api/file/upload - 上传文件
  - GET /api/file/download/{taskId} - 下载处理结果

#### LogController
- **功能**: 日志查询REST API
- **接口**:
  - GET /api/log/{taskId} - 查询任务日志

### 2. 服务层 (service)

#### ITaskFlowControlService
- **功能**: 任务流程控制服务接口
- **方法**:
  - createTask() - 创建任务
  - startTask() - 启动任务
  - getTaskStatus() - 获取任务状态
  - listAllTasks() - 列出所有任务
  - stopTask() - 停止任务

#### IDataParseService
- **功能**: 数据解析服务接口
- **方法**:
  - parse() - 解析数据文件

#### IDataCleanService
- **功能**: 数据清洗服务接口
- **方法**:
  - clean() - 清洗数据
  - normalize() - 标准化数据

#### IDataOutputService
- **功能**: 数据输出服务接口
- **方法**:
  - export() - 导出数据

#### ILogService
- **功能**: 日志服务接口
- **方法**:
  - info() - 记录信息日志
  - warn() - 记录警告日志
  - error() - 记录错误日志
  - queryLogs() - 查询日志

### 3. 任务执行模块 (task)

#### TaskExecutor
- **功能**: 异步任务执行器
- **特性**:
  - 使用@Async注解实现异步执行
  - 协调各个处理阶段
  - 更新任务进度和状态

#### TaskThreadPoolConfig
- **功能**: 线程池配置
- **配置**:
  - 核心线程数: 5
  - 最大线程数: 10
  - 队列容量: 100

### 4. 数据解析模块 (parser)

#### IDataParser (接口)
- **功能**: 数据解析器统一接口
- **方法**:
  - parse() - 解析文件
  - getSupportedFileType() - 获取支持的文件类型

#### CsvParser
- **功能**: CSV文件解析器
- **实现**: 使用BufferedReader读取CSV文件

#### ExcelParser
- **功能**: Excel文件解析器
- **实现**: 使用Apache POI读取Excel文件

#### JsonParser
- **功能**: JSON文件解析器
- **实现**: 使用Jackson解析JSON文件

#### ParserFactory
- **功能**: 解析器工厂
- **实现**: 根据文件类型动态选择解析器

### 5. 数据清洗模块 (cleaner)

#### MissingValueCleaner
- **功能**: 缺失值处理器
- **策略**: 填充空字符串，标记为无效

#### FormatValidator
- **功能**: 格式校验器
- **策略**: 检查空值，标记无效记录

#### DataNormalizer
- **功能**: 数据标准化器
- **策略**: 去除空格，统一格式

#### CleanerChain
- **功能**: 清洗链
- **实现**: 责任链模式，按顺序执行清洗器

### 6. 数据导出模块 (exporter)

#### IDataExporter (接口)
- **功能**: 数据导出器统一接口
- **方法**:
  - export() - 导出数据
  - getSupportedFormat() - 获取支持的格式

#### CsvExporter
- **功能**: CSV导出器
- **实现**: 使用BufferedWriter写入CSV文件

#### ExcelExporter
- **功能**: Excel导出器
- **实现**: 使用Apache POI生成Excel文件

#### JsonExporter
- **功能**: JSON导出器
- **实现**: 使用Jackson生成JSON文件

#### ExporterFactory
- **功能**: 导出器工厂
- **实现**: 根据输出格式动态选择导出器

### 7. 数据统计模块 (statistics)

#### DataStatistics
- **功能**: 统计数据模型
- **字段**: 总记录数、有效记录数、无效记录数、缺失值数量

#### StatisticsService
- **功能**: 统计服务
- **方法**: generateStatistics() - 生成统计信息

### 8. 进度监控模块 (progress)

#### TaskProgress
- **功能**: 进度数据模型
- **字段**: 任务ID、状态、百分比、当前阶段、消息

#### ProgressService
- **功能**: 进度服务
- **方法**: updateProgress() - 更新进度

### 9. 数据仓库模块 (repository)

#### TaskRepository
- **功能**: 任务数据仓库
- **实现**: 使用ConcurrentHashMap存储任务
- **方法**: save(), findById(), findAll(), delete()

## 三、数据流转流程

```
用户请求
    ↓
FileController.uploadFile() / TaskController.createTask()
    ↓
TaskFlowControlService.createTask()
    ↓
TaskRepository.save()
    ↓
TaskController.startTask()
    ↓
TaskExecutor.executeTask() [异步执行]
    ↓
ProgressService.updateProgress(PARSING)
    ↓
DataParseService.parse()
    ├─→ ParserFactory.getParser()
    └─→ IDataParser.parse()
    ↓
ProgressService.updateProgress(CLEANING)
    ↓
DataCleanService.clean()
    ├─→ MissingValueCleaner.clean()
    └─→ FormatValidator.clean()
    ↓
ProgressService.updateProgress(NORMALIZING)
    ↓
DataCleanService.normalize()
    └─→ DataNormalizer.normalize()
    ↓
StatisticsService.generateStatistics()
    ↓
ProgressService.updateProgress(EXPORTING)
    ↓
DataOutputService.export()
    ├─→ ExporterFactory.getExporter()
    └─→ IDataExporter.export()
    ↓
ProgressService.updateProgress(FINISHED)
    ↓
LogService.info()
    ↓
返回结果
```

## 四、技术选型说明

### 开发框架
- **Spring Boot 2.3.12**: 提供完整的Web框架和依赖注入
- **Spring Web**: REST API支持
- **Spring Async**: 异步任务执行

### 数据处理
- **Apache POI 5.2.3**: Excel文件读写
- **Jackson**: JSON数据处理
- **Java IO**: CSV文件处理

### 日志框架
- **Log4j2 2.17.1**: 日志记录

### 构建工具
- **Maven**: 项目构建和依赖管理

## 五、设计模式应用

### 1. 工厂模式 (Factory Pattern)
- **ParserFactory**: 根据文件类型创建对应的解析器
- **ExporterFactory**: 根据输出格式创建对应的导出器
- **优势**: 解耦对象创建和使用，易于扩展新的解析器/导出器

### 2. 策略模式 (Strategy Pattern)
- **IDataParser**: 定义解析策略接口
- **IDataCleaner**: 定义清洗策略接口
- **IDataExporter**: 定义导出策略接口
- **优势**: 算法可以独立于使用它的客户端变化

### 3. 责任链模式 (Chain of Responsibility)
- **CleanerChain**: 按顺序执行多个清洗器
- **优势**: 解耦请求发送者和接收者，灵活组合处理流程

### 4. 依赖注入 (Dependency Injection)
- **Spring IoC容器**: 管理所有Bean的生命周期
- **@Autowired**: 自动装配依赖
- **优势**: 降低耦合度，提高可测试性

## 六、扩展性设计

### 1. 新增解析器
只需实现IDataParser接口并添加@Component注解，ParserFactory会自动注册

### 2. 新增清洗器
创建清洗器类并添加@Component注解，在CleanerChain中注册即可

### 3. 新增导出器
只需实现IDataExporter接口并添加@Component注解，ExporterFactory会自动注册

### 4. 新增API接口
在对应的Controller中添加新的@RequestMapping方法即可

## 七、性能优化

### 1. 异步处理
- 使用@Async注解实现任务异步执行
- 配置线程池避免线程资源耗尽

### 2. 内存管理
- 使用流式处理大文件
- 及时释放不再使用的对象

### 3. 并发控制
- 使用ConcurrentHashMap存储任务
- 线程安全的进度更新

## 八、安全性考虑

### 1. 文件上传
- 限制文件大小（100MB）
- 验证文件类型
- 使用时间戳避免文件名冲突

### 2. 异常处理
- 全局异常处理器统一处理异常
- 详细的错误日志记录

### 3. 数据验证
- 输入参数校验
- 文件路径安全检查

## 九、测试策略

### 1. 单元测试
- 测试各个Service的业务逻辑
- 测试Parser、Cleaner、Exporter的功能

### 2. 集成测试
- 测试完整的数据处理流程
- 测试API接口

### 3. 性能测试
- 测试不同大小文件的处理性能
- 测试并发任务处理能力
