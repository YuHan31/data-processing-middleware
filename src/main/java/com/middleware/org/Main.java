package com.middleware.org;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


import java.io.IOException;

/**
 * 主程序入口（使用新的接口化架构）
 */
public class Main {

    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            System.out.println("========================================");
            System.out.println("  数据处理中间件系统 v2.0");
            System.out.println("  基于国产基础软件生态");
            System.out.println("========================================");
            System.out.println("输入 'help' 查看可用命令，输入 'exit' 退出");
            System.out.println();

            while (true) {
                String input = reader.readLine("middleware> ").trim();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("退出系统");
                    break;
                }

                CommandProcessor.processCommand(input);
            }
        } catch (IOException e) {
            System.err.println("终端初始化失败: " + e.getMessage());
        }
    }
}
