package com.nexus.ms_transacciones.client;

import com.nexus.ms_transacciones.config.RabbitMQConfig;
import com.nexus.ms_transacciones.dto.BancoDTO;
import com.nexus.ms_transacciones.dto.IsoMensajeDTO;
import com.nexus.ms_transacciones.dto.SwitchWebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SwitchClient {

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Value("${banco.webhook.url}")
    private String webhookUrl;

    @Value("${api.switch.url}")
    private String switchUrl;

    @Value("${api.switch.network.url:${api.switch.url}}")
    private String switchNetworkUrl;

    @Value("${banco.codigo:NEXUS}")
    private String bancoCodigo;

    @Value("${api.switch.apikey}")
    private String apiKey;

    public SwitchWebhookResponse enviarTransferencia(IsoMensajeDTO request) {
        String url = switchUrl + "/api/v2/switch/transfers";
        log.info("Enviando ISO 20022 SÍNCRONO al Switch: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<IsoMensajeDTO> entity = new HttpEntity<>(request, headers);
            ResponseEntity<SwitchWebhookResponse> response = restTemplate.postForEntity(
                    url, entity, SwitchWebhookResponse.class);

            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("❌ Switch RECHAZÓ la transacción ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            log.error("📋 Request URL: {}", url);
            log.error("📋 Request Body: {}", request);
            throw new RuntimeException("Transacción Rechazada por el Switch: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error contactando al Switch: {}", e.getMessage());
            throw new RuntimeException("Switch no disponible: " + e.getMessage());
        }
    }

    public void enviarTransferenciaAsincrona(IsoMensajeDTO request) {
        if (request.getHeader() != null) {
            request.getHeader().setCallbackUrl(this.webhookUrl);
        }

        log.info("Enviando mensaje ASÍNCRONO a RabbitMQ: ID {}", request.getBody().getInstructionId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_SWITCH, "switch.in", request);
    }

    public List<BancoDTO> obtenerBancos() {
        String url = switchNetworkUrl + "/api/v1/instituciones";
        try {
            ResponseEntity<List<BancoDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BancoDTO>>() {
                    });
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("No se pudieron obtener bancos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void enviarDevolucion(IsoMensajeDTO request) {
        String url = switchUrl + "/api/v1/transacciones/devoluciones";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<IsoMensajeDTO> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity(url, entity, Void.class);

            log.info("Devolución enviada al Switch correctamente");
        } catch (Exception e) {
            log.error("Error enviando devolución: {}", e.getMessage());
            throw new RuntimeException("Fallo al enviar devolución: " + e.getMessage());
        }
    }

    public String getBancoCodigo() {
        return bancoCodigo;
    }

    public void enviarDevolucion(com.nexus.ms_transacciones.dto.ReturnRequestDTO request) {
        String url = switchUrl + "/api/v2/switch/transfers/return";
        log.info("📤 Enviando Solicitud Devolución (pacs.004) al Switch: {}", request.getHeader().getMessageId());

        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            org.springframework.http.HttpEntity<com.nexus.ms_transacciones.dto.ReturnRequestDTO> entity = new org.springframework.http.HttpEntity<>(
                    request, headers);

            restTemplate.postForEntity(url, entity, String.class);
            log.info("✅ Devolución aceptada por el Switch");
        } catch (Exception e) {
            log.error("❌ Error enviando devolución al Switch: {}", e.getMessage());
            throw new RuntimeException("Error comunicándose con el Switch para Devolución: " + e.getMessage());
        }
    }

    public com.nexus.ms_transacciones.dto.AccountLookupResponse validarCuenta(
            com.nexus.ms_transacciones.dto.AccountLookupRequest request) {
        String url = switchUrl + "/api/v2/switch/accounts/lookup";
        log.info("🔍 Validando cuenta en Switch: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<com.nexus.ms_transacciones.dto.AccountLookupRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<com.nexus.ms_transacciones.dto.AccountLookupResponse> response = restTemplate.postForEntity(
                    url, entity, com.nexus.ms_transacciones.dto.AccountLookupResponse.class);

            return response.getBody();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("❌ Error validando cuenta: {}", e.getResponseBodyAsString());
            return com.nexus.ms_transacciones.dto.AccountLookupResponse.builder()
                    .status("FAILED")
                    .data(com.nexus.ms_transacciones.dto.AccountLookupResponse.AccountData.builder()
                            .exists(false)
                            .mensaje("Error validando cuenta: " + e.getResponseBodyAsString())
                            .build())
                    .build();
        } catch (Exception e) {
            log.error("❌ Error contactando al Switch para validación: {}", e.getMessage());
            throw new RuntimeException("Switch no disponible para validación: " + e.getMessage());
        }
    }
}