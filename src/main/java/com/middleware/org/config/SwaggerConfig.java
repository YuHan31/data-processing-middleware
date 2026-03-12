package com.middleware.org.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("数据处理中间件系统 API")
                        .version("1.0")
                        .description("通用数据处理中间件系统，支持CSV、Excel、JSON等多种格式的数据解析、清洗、标准化和导出")
                        .contact(new Contact()
                                .name("数据处理中间件")
                                .url("http://localhost:8080")));
    }
}
