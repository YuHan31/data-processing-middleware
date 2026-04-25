# 管理员模块 - 前端接口文档

## 前置说明

- 所有接口需要在登录后访问（需要在请求头或cookie中携带Session）
- 管理员接口需要登录账号的角色为 `ADMIN`
- 统一响应格式：`{ "code": 200, "message": "xxx", "data": {} }`
- 管理员登录后，所有接口均可访问

---

## 登录与角色跳转

### 登录接口响应
```
POST /api/user/login
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "account": "admin",
    "email": "admin@example.com",
    "phone": "13800000000",
    "name": "系统管理员",
    "nickname": "admin",
    "role": "ADMIN"
  }
}
```

**关键字段说明：**
| 字段 | 类型 | 说明 |
|------|------|------|
| userId | long | 用户ID，用于后续接口的标识 |
| role | string | 角色：ADMIN=管理员，USER=普通用户 |

### 前端跳转逻辑

登录成功后，根据 `role` 字段跳转到对应页面：

```javascript
// 伪代码示例
const handleLogin = async (loginData) => {
  const response = await api.post('/api/user/login', loginData);
  if (response.code === 200) {
    const { userId, role } = response.data;

    // 保存登录信息
    localStorage.setItem('userId', userId);
    localStorage.setItem('role', role);

    // 根据角色跳转
    if (role === 'ADMIN') {
      router.push('/admin');
    } else {
      router.push('/user');
    }
  }
};
```

**跳转规则：**
| role | 跳转目标 |
|------|----------|
| ADMIN | 管理员页面 |
| USER | 用户页面 |

---

## 一、用户管理

### 1.1 获取用户列表
```
GET /api/admin/users?page=1&size=10&keyword=
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页条数，默认10 |
| keyword | string | 否 | 搜索关键词（匹配账号/姓名/邮箱/手机） |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "account": "zhangsan",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "name": "张三",
        "nickname": "小张",
        "role": "USER",
        "enabled": true,
        "createTime": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  }
}
```

### 1.2 启用/禁用用户
```
POST /api/admin/users/{userId}/toggle
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | long | 是 | 用户ID（路径参数） |

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 1.3 获取用户详情
```
GET /api/admin/users/{userId}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "account": "zhangsan",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "name": "张三",
    "nickname": "小张",
    "role": "USER",
    "enabled": true,
    "createTime": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

## 二、任务管理

### 2.1 获取所有任务列表
```
GET /api/admin/tasks?page=1&size=10&userId=&status=
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页条数，默认10 |
| userId | long | 否 | 按用户ID筛选 |
| status | string | 否 | 按任务状态筛选（UPLOADED/RUNNING/FINISHED/FAILED/STOPPED） |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "taskId": "task_123456",
        "taskName": "用户数据清洗",
        "userId": 1,
        "userAccount": "zhangsan",
        "userName": "张三",
        "status": "FINISHED",
        "fileType": "csv",
        "outputFormat": "csv",
        "fileSize": 1024000,
        "createTime": "2024-01-01T10:00:00",
        "startTime": "2024-01-01T10:05:00",
        "endTime": "2024-01-01T10:10:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10,
    "pages": 5
  }
}
```

### 2.2 获取指定用户的所有任务
```
GET /api/admin/users/{userId}/tasks?page=1&size=10
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | long | 是 | 用户ID（路径参数） |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页条数，默认10 |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "taskId": "task_123456",
        "taskName": "用户数据清洗",
        "status": "FINISHED",
        "createTime": "2024-01-01T10:00:00",
        "startTime": "2024-01-01T10:05:00",
        "endTime": "2024-01-01T10:10:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

### 2.3 获取任务关联的清洗规则
```
GET /api/admin/tasks/{taskId}/rules
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| taskId | string | 是 | 任务ID（路径参数） |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": ["TRIM", "REMOVE_NULL", "DEDUPLICATE"]
}
```

---

## 三、日志管理

### 3.1 获取指定任务的完整日志
```
GET /api/admin/logs/task/{taskId}
```

**参数：**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| taskId | string | 是 | 任务ID（路径参数） |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "level": "INFO",
      "message": "开始解析文件",
      "userMessage": "正在解析文件...",
      "taskId": "task_123456",
      "timestamp": 1704067200000,
      "stage": "PARSE"
    },
    {
      "id": 2,
      "level": "INFO",
      "message": "解析完成，共1000条记录",
      "userMessage": "文件解析完成",
      "taskId": "task_123456",
      "timestamp": 1704067201000,
      "stage": "PARSE"
    },
    {
      "id": 3,
      "level": "ERROR",
      "message": "清洗规则TRIM执行异常",
      "userMessage": "数据清洗失败，请检查输入数据格式",
      "taskId": "task_123456",
      "timestamp": 1704067202000,
      "stage": "CLEAN",
      "exceptionMessage": "空指针异常",
      "stackTrace": "..."
    }
  ]
}
```

**日志字段说明：**
| 字段 | 类型 | 说明 |
|------|------|------|
| level | string | 日志级别：INFO/WARN/ERROR |
| message | string | 系统原始日志（管理员可见） |
| userMessage | string | 用户友好提示 |
| taskId | string | 关联的任务ID |
| timestamp | long | 时间戳（毫秒） |
| stage | string | 任务阶段：PARSE/CLEAN/EXPORT |
| exceptionMessage | string | 异常简短信息（仅ERROR级别） |
| stackTrace | string | 堆栈信息（管理员可见） |

### 3.2 获取指定用户的所有日志
```
GET /api/admin/logs/user/{userId}
```

### 3.3 获取所有日志
```
GET /api/admin/logs?page=1&size=100
```

---

## 四、清洗规则管理

### 4.1 获取所有清洗规则
```
GET /api/admin/clean-rules
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "ruleCode": "TRIM",
      "ruleName": "去除空格",
      "description": "去除字段值的首尾空格",
      "ruleType": "FORMAT",
      "enabled": true,
      "level": "basic",
      "displayOrder": 10,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "ruleCode": "PHONE_MASK",
      "ruleName": "手机号脱敏",
      "description": "对手机号进行脱敏（显示前三位和后四位）",
      "ruleType": "SECURITY",
      "enabled": true,
      "level": "advanced",
      "displayOrder": 31,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  ]
}
```

### 4.2 添加清洗规则 ⚠️ 已禁用
```
POST /api/admin/clean-rules
```
**注意：此接口已禁用，不支持通过接口添加新规则。** 新规则需要后端开发者编写Java代码实现。

### 4.3 更新清洗规则
```
PUT /api/admin/clean-rules/{id}
```

**请求体：**
```json
{
  "ruleName": "修改后的名称",
  "description": "修改后的描述",
  "enabled": false,
  "displayOrder": 60
}
```

**注意：只能修改 ruleName、description、enabled、displayOrder，不能修改 ruleCode、ruleType、level**

### 4.4 删除清洗规则
```
DELETE /api/admin/clean-rules/{id}
```

### 4.5 启用/禁用规则
```
POST /api/admin/clean-rules/{id}/toggle
```

---

## 五、错误码说明

| code | 说明 |
|------|------|
| 200 | 成功 |
| 401 | 未登录或登录已过期 |
| 403 | 无管理员权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 六、数据库升级SQL

请在数据库中执行以下SQL来完成数据库升级：

```sql
-- 添加 role 和 enabled 字段
ALTER TABLE `users` ADD COLUMN `role` VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '用户角色：USER-普通用户，ADMIN-管理员';
ALTER TABLE `users` ADD COLUMN `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户启用状态：1-启用 0-禁用';

-- 创建管理员索引
CREATE INDEX idx_role ON `users` (`role`);
CREATE INDEX idx_enabled ON `users` (`enabled`);

-- 插入默认管理员（账号: admin，密码: admin123456）
INSERT INTO `users` (`account`, `email`, `phone`, `name`, `password`, `role`, `enabled`, `create_time`, `updated_at`)
VALUES ('admin', 'admin@example.com', '13800000000', '系统管理员', '$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', 'ADMIN', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `role` = 'ADMIN';
```

**注意：** 管理员密码的BCrypt哈希值需要通过Java代码生成：
```java
new BCryptPasswordEncoder().encode("admin123456")
```

---

## 七、管理员账号

- 账号：`admin`
- 密码：`admin123456`（请在部署时修改为强密码）
- 角色：`ADMIN`

---

## 八、建议的前端页面结构

```
管理员面板
├── 用户管理
│   ├── 用户列表页面（分页、搜索、启用禁用按钮）
│   └── 用户详情弹窗
├── 任务管理
│   ├── 全部任务列表（分页、用户筛选、状态筛选）
│   ├── 用户任务列表（查看指定用户的任务）
│   └── 任务规则查看弹窗
├── 日志管理
│   ├── 全部日志列表
│   ├── 用户日志列表
│   └── 任务日志详情弹窗
└── 规则管理
    ├── 规则列表（表格展示）
    ├── 添加规则表单
    ├── 编辑规则表单
    └── 删除确认弹窗
```