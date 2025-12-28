package com.ecusol.ventanilla.client;

import com.ecusol.ventanilla.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CoreClient {
    private final WebClient webClient;

    public CoreClient(@Value("${ecusol.core.url}") String coreUrl) {
        // coreUrl = http://localhost:8081/api/core
        this.webClient = WebClient.builder().baseUrl(coreUrl).build();
    }

    public ResumenClienteDTO buscarCliente(String cedula) {
        try {
            // Ruta relativa: /ventanilla/buscar-cliente/{cedula}
            // URL Final: http://localhost:8081/api/core/ventanilla/buscar-cliente/...
            return webClient.get()
                    .uri("/ventanilla/buscar-cliente/" + cedula) 
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class) // Leemos el mensaje de error del Core
                                .flatMap(error -> Mono.error(new RuntimeException(error)))
                    )
                    .bodyToMono(ResumenClienteDTO.class)
                    .block();
        } catch (Exception e) {
            // Si es un error de negocio que ya capturamos, lo relanzamos tal cual
            if (e.getMessage().contains("bloqueado") || e.getMessage().contains("inactiva")) {
                 throw new RuntimeException(e.getMessage());
            }
            throw new RuntimeException("Cliente no encontrado o error en Core");
        }
    }

    // CORRECCIÓN: Capturamos el mensaje de error (body) cuando el Core devuelve 400/500
    public String operar(TransaccionCajaRequest req) {
        return webClient.post()
                .uri("/ventanilla/operar")
                .bodyValue(req)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException(error)))
                )
                .bodyToMono(String.class)
                .block();
    }
    
    public InfoCuentaDTO validarCuenta(String numero) {
        try {
             return webClient.get()
                .uri("/ventanilla/info-cuenta/" + numero)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new RuntimeException("Cuenta no válida")))
                .bodyToMono(InfoCuentaDTO.class)
                .block();
        } catch(Exception e) {
            throw new RuntimeException("Cuenta no existe o no se pudo validar");
        }
    }

    // --- RUTAS ADMINISTRATIVAS CORREGIDAS ---

    public String cambiarEstadoCuenta(String numeroCuenta, String estado) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/ventanilla/cuentas/" + numeroCuenta + "/estado") // Solo /ventanilla/...
                        .queryParam("estado", estado)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException(error)))
                )
                .bodyToMono(String.class)
                .block();
    }
    
    public String cambiarEstadoCliente(String cedula, String estado) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/ventanilla/clientes/estado") // Solo /ventanilla/...
                        .queryParam("cedula", cedula)
                        .queryParam("estado", estado)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException(error)))
                )
                .bodyToMono(String.class)
                .block();
    }

    public String eliminarCuenta(String numeroCuenta) {
        return webClient.delete()
                .uri("/ventanilla/cuentas/" + numeroCuenta) // Solo /ventanilla/...
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException(error)))
                )
                .bodyToMono(String.class)
                .block();
    }

    // --- NUEVO: OBTENER SUCURSAL POR ID ---
    public SucursalDTO obtenerSucursal(Integer id) {
        try {
             return webClient.get()
                .uri("/sucursales/" + id) // Endpoint del CoreSucursalController
                .retrieve()
                .bodyToMono(SucursalDTO.class)
                .block();
        } catch(Exception e) {
            // Si falla, retornamos un objeto dummy para no romper el login
            SucursalDTO dummy = new SucursalDTO();
            dummy.setNombre("Sucursal " + id);
            return dummy;
        }
    }
}