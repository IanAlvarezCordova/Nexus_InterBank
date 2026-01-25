package com.nexus.ms_transacciones.client;

import com.nexus.ms_transacciones.dto.BancoDTO;
import com.nexus.ms_transacciones.dto.IsoMensajeDTO;
import com.nexus.ms_transacciones.dto.SwitchWebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

import java.util.Collections;
import java.util.List;

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

    @Value("${api.switch.apikey}")
    private String apiKey;

    public SwitchWebhookResponse enviarTransferencia(IsoMensajeDTO request) {
        String url = switchUrl + "/api/v1/transacciones";
        
        log.info("Enviando ISO 20022 al Switch: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apikey", apiKey);

            HttpEntity<IsoMensajeDTO> entity = new HttpEntity<>(request, headers);
            ResponseEntity<SwitchWebhookResponse> response = restTemplate.postForEntity(
                url, entity, SwitchWebhookResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error contactando al Switch: {}", e.getMessage());
            throw new RuntimeException("Switch no disponible: " + e.getMessage());
        }
    }

    public List<BancoDTO> obtenerBancos() {
        String url = switchUrl.replace("8082", "8081") + "/api/v1/instituciones"; 
        try {
            ResponseEntity<List<BancoDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<BancoDTO>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
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
}