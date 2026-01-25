package com.nexus.ms_transacciones.controller;

// Asegúrate de que el paquete coincida con donde guardaste el DTO unificado
import com.nexus.ms_transacciones.dto.IsoMensajeDTO; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/core/transacciones")
public class WebhookController {

    @PostMapping("/recepcion")
    public ResponseEntity<?> recibirTransferencia(@RequestBody IsoMensajeDTO mensaje) {
        // Validaciones básicas para evitar NullPointerException
        if (mensaje.getBody() == null || mensaje.getHeader() == null) {
            return ResponseEntity.badRequest().body("Mensaje ISO mal formado");
        }

        System.out.println(">>> WEBHOOK (Ecusol): Recibida transferencia ISO 20022");
        System.out.println("    InstructionID: " + mensaje.getBody().getInstructionId());
        System.out.println("    Origen: " + mensaje.getHeader().getOriginatingBankId());
        System.out.println("    Creditor Account: " + mensaje.getBody().getCreditor().getAccountId());
        System.out.println("    Monto: " + mensaje.getBody().getAmount().getValue() + " " + mensaje.getBody().getAmount().getCurrency());

        // TODO: Lógica de acreditación real (Feign Client a MS-Cuentas)
        
        // Retorno estándar simulado
        return ResponseEntity.ok().body(Map.of(
            "status", "RECEIVED",
            "instructionId", mensaje.getBody().getInstructionId()
        ));
    }
}