package com.middleware.org.controller;

import com.middleware.org.common.Result;
import com.middleware.org.model.TaskContext;
import com.middleware.org.service.ITaskFlowControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理控制器
 */
@Tag(name = "文件管理", description = "文件上传、下载、信息查询等接口")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private ITaskFlowControlService taskService;

    /**
     * 上传文件并创建任务
     */
    @Operation(summary = "上传文件", description = "上传数据文件并自动创建处理任务")
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "输出格式(csv/xlsx/json)") @RequestParam(value = "outputFormat", defaultValue = "csv") String outputFormat,
            HttpSession session) {
        try {
            // 获取当前用户ID
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.fail(401, "用户未登录");
            }

            // 使用项目根目录的绝对路径
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + File.separator + "uploads" + File.separator + userId + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            long uploadTime = System.currentTimeMillis();
            String filePath = uploadDir + uploadTime + "_" + originalFilename;
            File destFile = new File(filePath);
            file.transferTo(destFile);

            String fileType = getFileType(originalFilename);
            String outputDir = projectRoot + File.separator + "output" + File.separator + userId + File.separator;
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }
            String outputPath = outputDir + uploadTime + "_output." + outputFormat;

            TaskContext taskContext = new TaskContext();
            taskContext.setTaskName("文件处理任务");
            taskContext.setInputFilePath(filePath);
            taskContext.setOutputFilePath(outputPath);
            taskContext.setFileType(fileType);
            taskContext.setOutputFormat(outputFormat);
            taskContext.setOriginalFileName(originalFilename);
            taskContext.setFileSize(file.getSize());
            taskContext.setUploadTime(uploadTime);
            taskContext.setUserId(userId);

            String taskId = taskService.createTask(taskContext);

            Map<String, Object> data = new HashMap<>();
            data.put("taskId", taskId);
            data.put("filePath", filePath);
            return Result.success("文件上传成功", data);
        } catch (IOException e) {
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 查询文件信息
     */
    @Operation(summary = "查询文件信息", description = "查询任务关联的文件元数据信息")
    @GetMapping("/info/{taskId}")
    public Result<Map<String, Object>> getFileInfo(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }

        TaskContext ctx = taskService.getTaskContext(taskId);
        if (ctx == null) {
            return Result.fail(404, "任务不存在: " + taskId);
        }
        // 权限校验：只能查看自己的文件
        if (!userId.equals(ctx.getUserId())) {
            return Result.fail(403, "无权访问该文件");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("taskId", taskId);
        data.put("fileName", ctx.getOriginalFileName());
        data.put("fileType", ctx.getFileType() != null ? ctx.getFileType().toUpperCase() : "");
        data.put("fileSize", ctx.getFileSize());
        data.put("uploadTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ctx.getUploadTime())));
        data.put("inputFilePath", ctx.getInputFilePath());
        data.put("outputFilePath", ctx.getOutputFilePath());
        return Result.success(data);
    }

    /**
     * 下载处理结果
     */
    @Operation(summary = "下载结果文件", description = "下载任务处理后的结果文件")
    @GetMapping("/download/{taskId}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        TaskContext ctx = taskService.getTaskContext(taskId);
        if (ctx == null) {
            return ResponseEntity.notFound().build();
        }
        // 权限校验：只能下载自己的文件
        if (!userId.equals(ctx.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        File outputFile = new File(ctx.getOutputFilePath());
        if (!outputFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(outputFile);
        String filename = outputFile.getName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private String getFileType(String filename) {
        if (filename.endsWith(".csv")) {
            return "csv";
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return "xlsx";
        } else if (filename.endsWith(".json")) {
            return "json";
        }
        return "csv";
    }
}

