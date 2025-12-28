package com.ecusol.web.config; // O cambia el paquete según el proyecto (web/ventanilla)

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Si en el CORE no usas JWT Filter, borra esta inyección y el addFilterBefore
    // de abajo.
    // En WEB y VENTANILLA sí déjalo.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS: Activamos nuestra configuración "Universal"
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF: Desactivado (No necesario para APIs REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Gestión de Sesión: Stateless (Sin cookies de sesión)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Rutas Públicas y Privadas
                .authorizeHttpRequests(auth -> auth
                        // Swagger y utilidades (Público)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/web/util/**")
                        .permitAll()

                        // Endpoints de Auth (Login/Registro) (Público)
                        .requestMatchers("/api/web/auth/**").permitAll()

                        // NOTA: Para el CORE, si quieres que sea accesible desde los otros backends sin
                        // token
                        // (porque la seguridad es por red interna), puedes dejar esto abierto:
                        .requestMatchers("/api/core/**").permitAll()

                        // Para WEB y VENTANILLA, abrimos todo (EMERGENCIA)
                        .anyRequest().permitAll())

                // 5. Añadir el filtro JWT (Solo si no es el Core abierto)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CONFIGURACIÓN CORS "TODO PERMITIDO" (UNIVERSAL)
     * Esto permite que cualquier IP (AWS, Localhost, Celular) consuma la API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // EL TRUCO: Usar allowedOriginPatterns("*") en lugar de allowedOrigins
        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration
                .setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setAllowCredentials(true); // Permitir enviar Cookies/Tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}