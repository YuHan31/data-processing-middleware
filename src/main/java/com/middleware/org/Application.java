package com.middleware.org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot应用主类
 */
@SpringBootApplication
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("========================================");
        System.out.println("  数据处理中间件系统已启动");
        System.out.println("  访问地址: http://localhost:9999");
        System.out.println("  Swagger API文档：http://localhost:9999/swagger-ui.html");
        System.out.println("========================================");
    }
}
