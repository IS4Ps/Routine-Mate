package com.hansung.adhd.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ADHD 치료 보조 앱 API 명세서")
                        .description("백엔드 API 테스트를 위한 Swagger UI입니다.")
                        .version("1.0.0"));
    }
}