# 数据处理中间件系统

## 项目简介

这是一个通用数据处理中间件系统，用于对多种格式的数据文件进行统一处理。系统采用Spring Boot框架，提供完整的REST API接口，支持CSV、Excel、JSON等多种数据格式的解析、清洗、标准化和导出。

## 系统架构

```
数据接入 → 数据解析 → 数据清洗 → 数据标准化 → 数据统计 → 数据输出
```

## 核心功能

### 1. 多格式数据接入
- ✅ CSV文件解析
- ✅ Excel文件解析（.xlsx）
- ✅ JSON文件解析
- ✅ 文件上传接口

### 2. 统一数据解析
- ✅ 工厂模式动态选择解析器
- ✅ 统一数据结构（DataRecord）
- ✅ 自动识别文件类型

### 3. 数据清洗
- ✅ 缺失值处理
- ✅ 格式校验
- ✅ 数据标准化
- ✅ 责任链模式组织清洗流程

### 4. 数据统计分析
- ✅ 总记录数统计
- ✅ 有效/无效记录统计
- ✅ 缺失值统计
- ✅ 处理时间统计

### 5. 多格式结果输出
- ✅ CSV导出
- ✅ Excel导出
- ✅ JSON导出
- ✅ 工厂模式动态选择导出器

### 6. 任务管理
- ✅ 任务创建
- ✅ 任务启动
- ✅ 任务状态查询
- ✅ 任务列表查询
- ✅ 任务停止

### 7. 进度跟踪
- ✅ 实时进度监控
- ✅ 任务状态管理（UPLOADED → PARSING → CLEANING → NORMALIZING → EXPORTING → FINISHED）
- ✅ 进度百分比计算

### 8. 日志记录
- ✅ 任务日志记录
- ✅ 日志查询接口
- ✅ 分级日志（INFO/WARN/ERROR）

## 技术栈

- **开发语言**: Java 17
- **开发框架**: Spring Boot 2.3.12
- **API文档**: Springdoc OpenAPI 1.6.15 (Swagger)
- **数据解析**: Apache POI 5.2.3 (Excel), Jackson (JSON)
- **日志框架**: Log4j2 2.17.1
- **构建工具**: Maven
- **设计模式**: 工厂模式、策略模式、责任链模式

## 快速开始

### 1. 编译项目

```bash
mvn clean package
```

### 2. 启动应用

**方式1：使用启动脚本**
```bash
./start.sh
```

**方式2：直接运行JAR**
```bash
java -jar target/data-processing-middleware-1.0-SNAPSHOT.jar
```

**方式3：使用Maven**
```bash
mvn spring-boot:run
```

### 3. 访问系统

应用启动后访问：**http://localhost:8080**

### 4. 访问Swagger API文档

应用启动后访问：**http://localhost:8080/swagger-ui.html**

在Swagger界面可以：
- 查看所有API接口
- 在线测试接口
- 查看请求参数和响应格式

## API接口文档

### 统一返回格式

所有API接口统一返回以下格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- `code`: 状态码，200表示成功，500表示失败
- `message`: 返回消息
- `data`: 返回数据

### 任务管理接口

#### 1. 创建任务
```http
POST /api/task/create
Content-Type: application/json

{
  "taskName": "数据处理任务",
  "inputFilePath": "test-data.csv",
  "outputFilePath": "output/result.csv",
  "fileType": "csv"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "任务创建成功",
  "data": {
    "taskId": "TASK-A1B2C3D4"
  }
}
```

#### 2. 启动任务
```http
POST /api/task/start/{taskId}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "任务启动成功",
  "data": null
}
```

#### 3. 查询任务状态
```http
GET /api/task/status/{taskId}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "FINISHED",
    "progress": {
      "taskId": "TASK-A1B2C3D4",
      "status": "FINISHED",
      "percentage": 100,
      "currentStage": "FINISHED",
      "message": "任务完成"
    }
  }
}
```

#### 4. 获取任务列表
```http
GET /api/task/list
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tasks": [...],
    "total": 5
  }
}
```

#### 5. 停止任务
```http
POST /api/task/stop/{taskId}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "任务已停止",
  "data": null
}
```

### 文件管理接口

#### 1. 上传文件
```http
POST /api/file/upload
Content-Type: multipart/form-data

file: [文件]
outputFormat: csv
```

**响应示例**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "taskId": "TASK-A1B2C3D4",
    "filePath": "uploads/1234567890_data.csv"
  }
}
```

#### 2. 下载结果
```http
GET /api/file/download/{taskId}
```

### 日志管理接口

#### 查询任务日志
```http
GET /api/log/{taskId}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "logs": [
      {
        "level": "INFO",
        "message": "任务开始执行",
        "timestamp": 1234567890
      }
    ],
    "total": 10
  }
}
```

## 使用示例

### 示例1：处理CSV文件

```bash
# 1. 创建任务
curl -X POST http://localhost:8080/api/task/create \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "CSV数据处理",
    "inputFilePath": "test-data.csv",
    "outputFilePath": "output/result.csv",
    "fileType": "csv"
  }'

# 2. 启动任务（假设返回的taskId是TASK-12345678）
curl -X POST http://localhost:8080/api/task/start/TASK-12345678

# 3. 查询进度
curl http://localhost:8080/api/task/status/TASK-12345678

# 4. 查询日志
curl http://localhost:8080/api/log/TASK-12345678
```

### 示例2：上传文件并处理

```bash
# 上传文件（自动创建并启动任务）
curl -X POST http://localhost:8080/api/file/upload \
  -F "file=@test-data.csv" \
  -F "outputFormat=csv"
```

### 示例3：使用测试脚本

```bash
# 运行完整的API测试
./test-api.sh
```

## 项目结构

```
data-processing-middleware/
├── src/main/java/com/middleware/org/
│   ├── Application.java              # Spring Boot启动类
│   ├── controller/                   # REST API控制器
│   │   ├── TaskController.java       # 任务管理
│   │   ├── FileController.java       # 文件管理
│   │   └── LogController.java        # 日志查询
│   ├── service/                      # 服务接口
│   │   ├── ITaskFlowControlService.java
│   │   ├── IDataParseService.java
│   │   ├── IDataCleanService.java
│   │   ├── IDataOutputService.java
│   │   └── ILogService.java
│   ├── service/impl/                 # 服务实现
│   │   ├── TaskFlowControlServiceImpl.java
│   │   ├── DataParseServiceImpl.java
│   │   ├── DataCleanServiceImpl.java
│   │   ├── DataOutputServiceImpl.java
│   │   └── LogServiceImpl.java
│   ├── task/                         # 任务执行模块
│   │   ├── TaskExecutor.java         # 异步任务执行器
│   │   └── TaskThreadPoolConfig.java # 线程池配置
│   ├── parser/                       # 数据解析器
│   │   ├── IDataParser.java
│   │   ├── CsvParser.java
│   │   ├── ExcelParser.java
│   │   ├── JsonParser.java
│   │   └── ParserFactory.java
│   ├── cleaner/                      # 数据清洗器
│   │   ├── MissingValueCleaner.java
│   │   ├── FormatValidator.java
│   │   ├── DataNormalizer.java
│   │   └── CleanerChain.java
│   ├── exporter/                     # 数据导出器
│   │   ├── IDataExporter.java
│   │   ├── CsvExporter.java
│   │   ├── ExcelExporter.java
│   │   ├── JsonExporter.java
│   │   └── ExporterFactory.java
│   ├── statistics/                   # 数据统计模块
│   │   ├── DataStatistics.java
│   │   └── StatisticsService.java
│   ├── progress/                     # 进度监控模块
│   │   ├── TaskProgress.java
│   │   └── ProgressService.java
│   ├── repository/                   # 数据仓库
│   │   └── TaskRepository.java
│   ├── model/                        # 数据模型
│   │   ├── TaskContext.java
│   │   ├── TaskResult.java
│   │   ├── TaskStatus.java
│   │   ├── DataRecord.java
│   │   ├── ProcessedData.java
│   │   ├── FileMetadata.java
│   │   ├── ValidationResult.java
│   │   ├── CleanRule.java
│   │   └── LogEntry.java
│   ├── exception/                    # 异常处理
│   │   ├── FileProcessException.java
│   │   ├── DataParseException.java
│   │   ├── DataCleanException.java
│   │   └── GlobalExceptionHandler.java
│   ├── util/                         # 工具类
│   │   ├── FileUtil.java
│   │   ├── DateUtil.java
│   │   └── ValidationUtil.java
│   └── config/                       # 配置类
│       ├── WebConfig.java
│       └── FileStorageConfig.java
├── src/main/resources/
│   └── application.properties        # 应用配置
├── uploads/                          # 上传目录
├── output/                           # 输出目录
├── test-data.csv                     # 测试数据
├── start.sh                          # 启动脚本
├── test-api.sh                       # API测试脚本
├── pom.xml                           # Maven配置
├── README.md                         # 项目说明
├── PROJECT_STRUCTURE.md              # 项目结构文档
├── CORE_CLASSES_DESIGN.md            # 核心类设计文档
└── 使用指南.md                        # 使用指南
```

## 数据处理流程

```
1. 用户上传文件/创建任务
   ↓
2. 文件类型识别
   ↓
3. 任务创建（生成taskId）
   ↓
4. 任务启动（异步执行）
   ↓
5. 数据解析阶段（PARSING）
   - ParserFactory选择解析器
   - 解析为DataRecord列表
   ↓
6. 数据清洗阶段（CLEANING）
   - MissingValueCleaner处理缺失值
   - FormatValidator格式校验
   ↓
7. 数据标准化阶段（NORMALIZING）
   - DataNormalizer标准化处理
   ↓
8. 数据统计阶段
   - StatisticsService生成统计信息
   ↓
9. 数据导出阶段（EXPORTING）
   - ExporterFactory选择导出器
   - 导出为指定格式
   ↓
10. 任务完成（FINISHED）
    - 更新任务状态
    - 记录日志
```

## 设计模式应用

1. **工厂模式**
   - ParserFactory：根据文件类型创建解析器
   - ExporterFactory：根据输出格式创建导出器

2. **策略模式**
   - IDataParser：不同的解析策略
   - IDataCleaner：不同的清洗策略
   - IDataExporter：不同的导出策略

3. **责任链模式**
   - CleanerChain：按顺序执行多个清洗器

4. **依赖注入**
   - Spring框架管理所有Bean
   - 自动装配依赖关系

## 扩展开发

### 添加新的数据解析器

1. 实现`IDataParser`接口
2. 添加`@Component`注解
3. 实现`parse()`和`getSupportedFileType()`方法
4. ParserFactory会自动注册

```java
@Component
public class XmlParser implements IDataParser {
    @Override
    public ProcessedData parse(String filePath) throws Exception {
        // 实现XML解析逻辑
    }

    @Override
    public String getSupportedFileType() {
        return "xml";
    }
}
```

### 添加新的数据清洗器

1. 创建清洗器类
2. 添加`@Component`注解
3. 在`CleanerChain`中注册

```java
@Component
public class DuplicateRemover {
    public void clean(ProcessedData processedData) {
        // 实现去重逻辑
    }
}
```

### 添加新的数据导出器

1. 实现`IDataExporter`接口
2. 添加`@Component`注解
3. 实现`export()`和`getSupportedFormat()`方法
4. ExporterFactory会自动注册

```java
@Component
public class XmlExporter implements IDataExporter {
    @Override
    public void export(ProcessedData data, String outputPath) throws Exception {
        // 实现XML导出逻辑
    }

    @Override
    public String getSupportedFormat() {
        return "xml";
    }
}
```

## 测试数据

项目包含测试CSV文件 `test-data.csv`：

```csv
姓名,年龄,城市,邮箱
张三,25,北京,zhangsan@example.com
李四,30,上海,lisi@example.com
王五,,广州,wangwu@example.com
赵六,28,深圳,
钱七,35,杭州,qianqi@example.com
```

## 注意事项

1. 确保输入文件路径正确且有读取权限
2. 确保输出路径有写入权限
3. 大文件处理可能需要较长时间
4. 建议使用异步方式处理大数据量任务
5. 系统默认端口为8080，可在application.properties中修改

## 系统要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- 至少512MB可用内存

## 许可证

本项目仅用于学习和研究目的。

## 联系方式

如有问题，请查看项目文档或提交Issue。
