package com.acme.rbs.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${rbs.openapi.title}")
    private String title;

    @Value("${rbs.openapi.description}")
    private String description;

    @Value("${rbs.openapi.version}")
    private String version;

    @Bean
    public OpenAPI rbsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version));
    }
}
