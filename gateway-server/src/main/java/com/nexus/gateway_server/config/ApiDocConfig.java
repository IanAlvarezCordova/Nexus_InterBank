package com.nexus.gateway_server.config; // Verifica que este sea tu paquete correcto

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ApiDocConfig {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties config = new SwaggerUiConfigProperties();
        
        // 1. Desactivamos la URL por defecto
        config.setDisableSwaggerDefaultUrl(true);
        config.setPath("/swagger-ui.html");
        config.setConfigUrl("/v3/api-docs/swagger-config");

        // 2. Definimos el menú manualmente (USANDO SET)
        Set<SwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();
        
        // --- AGREGA AQUÍ TUS SERVICIOS ---
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("1. Transacciones", "/v3/api-docs/transacciones", "transacciones"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("2. Cuentas", "/v3/api-docs/cuentas", "cuentas"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("3. Clientes", "/v3/api-docs/clientes", "clientes"));
        urls.add(new SwaggerUiConfigProperties.SwaggerUrl("4. Geografía", "/v3/api-docs/geografia", "geografia"));

        config.setUrls(urls);
        return config;
    }
}