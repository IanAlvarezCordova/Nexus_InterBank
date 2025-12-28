//ubi: src/main/java/com/ecusol/web/client/TransaccionesClient.java
package com.ecusol.web.client;

import com.ecusol.web.dto.MovimientoTxDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Component
public class TransaccionesClient {

    private final WebClient webClient;

    public TransaccionesClient(@Value("${ecusol.core.url}") String coreUrl) {
        // coreUrl expected: http://nexus-gateway:8080/api
        this.webClient = WebClient.builder().baseUrl(coreUrl).build();
    }

    public List<MovimientoTxDTO> obtenerMovimientosPorCuenta(String numeroCuenta) {
        try {
            return webClient.get()
                    .uri("/v1/transacciones/cuenta/" + numeroCuenta)
                    .retrieve()
                    .bodyToFlux(MovimientoTxDTO.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
