package com.nexus.ms_transacciones.client;

import com.nexus.ms_transacciones.dto.BancoDTO;
import com.nexus.ms_transacciones.dto.SwitchTransferRequest;
import com.nexus.ms_transacciones.dto.SwitchTransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Cliente para comunicarse con el Switch DIGICONECU.
 * Soporta envío de transferencias y consulta de bancos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SwitchClient {

    private final RestTemplate restTemplate;

    @Value("${api.switch.url}")
    private String switchUrl;

    @Value("${api.switch.network.url:${api.switch.url}}")
    private String switchNetworkUrl;

    @Value("${banco.codigo:NEXUS}")
    private String bancoCodigo;

    @Value("${api.key:NEXUS_SECRET_KEY}")
    private String apiKey;

    /**
     * Envía una transferencia interbancaria al Switch DIGICONECU.
     * Endpoint: POST /api/switch/v1/transferir
     */
    public com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO enviarTransferencia(
            com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO request) {
        // CORRECCIÓN URL: Según guía usuario "http://IP:8000/api/switch/v1/transferir"
        // Aseguramos que switchUrl apunte a la base correcta o ajustamos aquí.
        // Asumiremos que switchUrl ya trae la base (ej: http://34.44.123.236:9080)
        // CORRECCIÓN URL: Usar /api/v1/transacciones como el postman exitoso
        // Asumiremos que switchUrl ya trae la base (ej: http://34.44.123.236:9080 o el
        // dominio)
        String url = switchUrl + "/api/v1/transacciones";

        log.info("📤 Enviando transferencia ISO 20022 al Switch: {} -> {}",
                request.getBody().getDebtor().getAccountId(),
                request.getBody().getCreditor().getAccountId());

        try {
            // Headers
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            org.springframework.http.HttpEntity<com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO> entity = new org.springframework.http.HttpEntity<>(
                    request, headers);

            ResponseEntity<com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO> response = restTemplate.postForEntity(url,
                    entity,
                    com.nexus.ms_transacciones.dto.iso.IsoMensajeDTO.class);

            log.info("✅ Respuesta del Switch: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("❌ Error enviando al Switch: {}", e.getMessage());
            // Retornamos null o lanzamos excepción según lógica negocio
            // Para mantener compatibilidad con servicio existente:
            throw new RuntimeException("Error comunicándose con el Switch: " + e.getMessage());
        }
    }

    /**
     * Obtiene la lista de bancos disponibles en el ecosistema DIGICONECU.
     * Endpoint: GET /api/v1/red/bancos (Network Management - puerto 9082)
     */
    public List<BancoDTO> obtenerBancos() {
        String url = switchNetworkUrl + "/api/v1/red/bancos";
        log.info("📡 Consultando bancos disponibles en el Switch: {}", url);

        try {
            ResponseEntity<List<BancoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<BancoDTO>>() {
                    });

            List<BancoDTO> bancos = response.getBody();
            log.info("✅ Bancos obtenidos: {}", bancos != null ? bancos.size() : 0);
            return bancos != null ? bancos : Collections.emptyList();
        } catch (Exception e) {
            log.error("❌ Error obteniendo bancos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene el código del banco configurado (NEXUS).
     */
    public String getBancoCodigo() {
        return bancoCodigo;
    }
}