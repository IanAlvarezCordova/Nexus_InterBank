package com.nexus.ms_transacciones.client;

import com.nexus.ms_transacciones.exception.SaldoInsuficienteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

@Component
@Slf4j
public class CuentaClient {

    private final RestTemplate restTemplate;
    private final String urlCompleta;

    // Eliminamos @RequiredArgsConstructor para usar este constructor manual
    // @Value inyectará la URL desde application.properties o variables de entorno
    // de AWS
    public CuentaClient(RestTemplate restTemplate, @Value("${api.cuentas.url}") String urlBase) {
        this.restTemplate = restTemplate;
        this.urlCompleta = urlBase + "/api/v1/cuentas";
        log.info(">>> CuentaClient inicializado con URL: {}", this.urlCompleta);
    }

    public void debitar(String cuenta, BigDecimal monto) {
        try {
            log.info(">>> Iniciando DEBITO en: {}/debito para cuenta: {}", urlCompleta, cuenta);
            record Req(String cuenta, BigDecimal monto) {
            }
            restTemplate.postForEntity(urlCompleta + "/debito", new Req(cuenta, monto), Void.class);
            log.info(">>> DEBITO exitoso");
        } catch (HttpClientErrorException.Conflict | HttpClientErrorException.BadRequest e) {
            log.error(">>> FALLO DEBITO (Saldo): {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new SaldoInsuficienteException("Fondos insuficientes en la cuenta origen");
        } catch (Exception e) {
            log.error(">>> ERROR CONEXION MS-CUENTAS en DEBITO: {}", e.getMessage(), e);
            throw new RuntimeException("Servicio de cuentas no disponible: " + e.getMessage());
        }
    }

    public void acreditar(String cuenta, BigDecimal monto) {
        try {
            log.info(">>> Iniciando CREDITO en: {}/credito para cuenta: {}", urlCompleta, cuenta);
            record Req(String cuenta, BigDecimal monto) {
            }
            restTemplate.postForEntity(urlCompleta + "/credito", new Req(cuenta, monto), Void.class);
            log.info(">>> CREDITO exitoso");
        } catch (Exception e) {
            log.error(">>> ERROR CONEXION MS-CUENTAS en CREDITO: {}", e.getMessage(), e);
            throw new RuntimeException("Error crítico al acreditar fondos: " + e.getMessage());
        }
    }

    public void compensar(String cuenta, BigDecimal monto) {
        log.warn("SAGA COMPENSANDO: Revirtiendo débito para cuenta {}", cuenta);
        acreditar(cuenta, monto);
    }
}