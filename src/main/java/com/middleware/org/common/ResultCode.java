package com.middleware.org.common;

/**
 * 响应状态码枚举
 */
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 参数为空
     */
    PARAM_IS_BLANK(400, "参数为空"),

    /**
     * 参数类型错误
     */
    PARAM_TYPE_ERROR(400, "参数类型错误"),

    /**
     * 参数缺失
     */
    PARAM_NOT_COMPLETE(400, "参数缺失"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源未找到
     */
    NOT_FOUND(404, "资源未找到"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 任务不存在
     */
    TASK_NOT_FOUND(404, "任务不存在"),

    /**
     * 任务已完成
     */
    TASK_ALREADY_FINISHED(400, "任务已完成"),

    /**
     * 文件解析失败
     */
    FILE_PARSE_ERROR(500, "文件解析失败"),

    /**
     * 数据清洗失败
     */
    DATA_CLEAN_ERROR(500, "数据清洗失败"),

    /**
     * 数据导出失败
     */
    DATA_EXPORT_ERROR(500, "数据导出失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}