package com.example.products;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("product-api")
                .pathsToMatch("/api/products/**")
                .build();
    }


}