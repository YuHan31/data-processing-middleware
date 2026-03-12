package com.middleware.org;

import com.middleware.org.model.TaskContext;
import com.middleware.org.model.TaskResult;
import com.middleware.org.service.ITaskFlowControlService;
import com.middleware.org.service.ServiceFactory;
import com.middleware.org.FileSelector;

import java.util.List;
import java.util.Scanner;

/**
 * 命令处理器（重构版）
 * 使用接口化的服务架构
 */
public class CommandProcessor {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ServiceFactory serviceFactory = ServiceFactory.getInstance();
    private static final ITaskFlowControlService taskService = serviceFactory.getTaskFlowControlService();

    public static void processCommand(String command) {
        switch (command) {
            case "list":
                listTasks();
                break;
            case "start":
                startNewTask();
                break;
            case "stop":
                stopTaskByUserInput();
                break;
            case "status":
                checkTaskStatus();
                break;
            case "help":
                showHelp();
                break;
            case "exit":
                System.out.println("[INFO] 退出系统...");
                System.exit(0);
                break;
            default:
                System.out.println("[ERROR] 无效命令：" + command);
        }
    }

    private static void startNewTask() {
        System.out.print("[INFO] 请输入任务名称: ");
        String taskName = scanner.nextLine().trim();
        if (taskName.isEmpty()) {
            taskName = "task_" + System.currentTimeMillis();
        }

        System.out.println("[INFO] 请选择输入文件...");
        String inputPath = FileSelector.selectFile();
        if (inputPath == null) {
            System.out.println("[ERROR] 未选择输入文件，任务启动取消！");
            return;
        }

        System.out.println("[INFO] 请选择输出路径...");
        String outputPath = FileSelector.selectSavePath();
        if (outputPath == null) {
            System.out.println("[ERROR] 未选择输出路径，任务启动取消！");
            return;
        }

        // 创建任务上下文
        TaskContext context = new TaskContext(taskName, inputPath, outputPath);
        context.setFileType(detectFileType(inputPath));

        // 启动任务
        String taskId = taskService.startTask(context);
        System.out.println("[INFO] 任务已启动: " + taskId + " - " + taskName);
        System.out.println("[INFO] 输入文件：" + inputPath);
        System.out.println("[INFO] 输出路径：" + outputPath);
    }

    private static void stopTaskByUserInput() {
        System.out.print("[INFO] 请输入要停止的任务 ID: ");
        String taskId = scanner.nextLine().trim();
        if (taskId.isEmpty()) {
            System.out.println("[ERROR] 任务 ID 不能为空！");
            return;
        }

        boolean success = taskService.stopTask(taskId);
        if (success) {
            System.out.println("[INFO] 任务 " + taskId + " 已停止");
        } else {
            System.out.println("[ERROR] 停止任务失败");
        }
    }

    private static void listTasks() {
        List<TaskResult> tasks = taskService.listAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("[INFO] 当前没有任务");
            return;
        }

        System.out.println("[INFO] 当前任务列表：");
        System.out.printf("%-10s %-20s %-15s %-30s%n", "Task ID", "Name", "Status", "Input File");
        System.out.println("--------------------------------------------------------------------------------");

        for (TaskResult task : tasks) {
            System.out.printf("%-10s %-20s %-15s %-30s%n",
                    task.getTaskId(),
                    task.getTaskName(),
                    task.getStatus(),
                    task.getInputFilePath());
        }
    }

    private static void checkTaskStatus() {
        System.out.print("[INFO] 请输入要查询的任务 ID: ");
        String taskId = scanner.nextLine().trim();
        if (taskId.isEmpty()) {
            System.out.println("[ERROR] 任务 ID 不能为空！");
            return;
        }

        TaskResult result = taskService.getTaskStatus(taskId);
        if (result == null) {
            System.out.println("[ERROR] 未找到任务：" + taskId);
            return;
        }

        System.out.println("[INFO] 任务详情：");
        System.out.println("  任务ID: " + result.getTaskId());
        System.out.println("  任务名称: " + result.getTaskName());
        System.out.println("  状态: " + result.getStatus());
        System.out.println("  输入文件: " + result.getInputFilePath());
        System.out.println("  输出文件: " + result.getOutputFilePath());
        if (result.getProcessedRecords() != null) {
            System.out.println("  处理记录数: " + result.getProcessedRecords());
        }
    }

    private static void showHelp() {
        System.out.println("\n[INFO] 数据处理中间件命令列表：");
        System.out.println("  list    - 显示所有任务");
        System.out.println("  start   - 启动一个新任务");
        System.out.println("  stop    - 停止指定任务");
        System.out.println("  status  - 查看任务状态");
        System.out.println("  help    - 显示帮助信息");
        System.out.println("  exit    - 退出系统");
    }

    private static String detectFileType(String filePath) {
        if (filePath.endsWith(".csv")) {
            return "CSV";
        } else if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
            return "EXCEL";
        } else if (filePath.endsWith(".json")) {
            return "JSON";
        }
        return "UNKNOWN";
    }
}
