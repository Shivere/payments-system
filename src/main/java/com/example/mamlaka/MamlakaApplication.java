package com.example.mamlaka;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableCaching
@OpenAPIDefinition(
        info = @Info(
                title = "Mamlaka Payments REST API Documentation",
                description = "Mamlaka Payments REST API Documentation",
                version = "v1",
                contact = @Contact(
                        name = "Shivere Shawn",
                        email = "shivereshawn@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description =  "Mamlaka Payments REST API Documentation",
                url = "https://localhost:8080/swagger-ui.html"
        )
)
public class MamlakaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MamlakaApplication.class, args);
    }

}
