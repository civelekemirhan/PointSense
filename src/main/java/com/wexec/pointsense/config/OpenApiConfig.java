package com.wexec.pointsense.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PointSense API",
                version = "v1",
                description = "PointSense harita ve simülasyon backend API dokümantasyonu",
                contact = @Contact(name = "PointSense Team"),
                license = @License(name = "Proprietary")
        )
)
public class OpenApiConfig {
}
