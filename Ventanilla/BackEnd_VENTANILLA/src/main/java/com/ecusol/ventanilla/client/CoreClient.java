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
        this.webClient = WebClient.builder().baseUrl(coreUrl).build();
    }

    public ResumenClienteDTO buscarCliente(String cedula) {
        try {
            return webClient.get()
                    .uri("/ventanilla/buscar-cliente/" + cedula) 
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class) 
                                .flatMap(error -> Mono.error(new RuntimeException(error)))
                    )
                    .bodyToMono(ResumenClienteDTO.class)
                    .block();
        } catch (Exception e) {
            if (e.getMessage().contains("bloqueado") || e.getMessage().contains("inactiva")) {
                 throw new RuntimeException(e.getMessage());
            }
            throw new RuntimeException("Cliente no encontrado o error en Core");
        }
    }

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


    public String cambiarEstadoCuenta(String numeroCuenta, String estado) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/ventanilla/cuentas/" + numeroCuenta + "/estado")
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
                        .path("/ventanilla/clientes/estado") 
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
                .uri("/ventanilla/cuentas/" + numeroCuenta) 
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> 
                    response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException(error)))
                )
                .bodyToMono(String.class)
                .block();
    }

    public SucursalDTO obtenerSucursal(Integer id) {
        try {
             return webClient.get()
                .uri("/sucursales/" + id) 
                .retrieve()
                .bodyToMono(SucursalDTO.class)
                .block();
        } catch(Exception e) {
            SucursalDTO dummy = new SucursalDTO();
            dummy.setNombre("Sucursal " + id);
            return dummy;
        }
    }
}