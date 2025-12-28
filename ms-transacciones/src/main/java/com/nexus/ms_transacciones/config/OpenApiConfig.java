package com.nexus.ms_transacciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-TRANSACCIONES | NEXUS")
                        .version("1.0.0")
                        .description("Microservicio encargado de la orquestación de transferencias interbancarias mediante el patrón SAGA. " +
                                     "Incluye integración con MS-CUENTAS y el Switch del Banco Central.")
                        .contact(new Contact().name("Departamento de Desarrollo Ecuasol")));
                }
}