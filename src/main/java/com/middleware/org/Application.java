package com.middleware.org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot应用主类
 * 启动Web服务，提供REST API接口
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("========================================");
        System.out.println("  数据处理中间件系统已启动");
        System.out.println("  访问地址: http://localhost:9999");
        System.out.println("========================================");
    }
}
