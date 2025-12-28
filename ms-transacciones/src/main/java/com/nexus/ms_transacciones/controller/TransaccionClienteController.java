package com.nexus.ms_transacciones.controller;

import com.nexus.ms_transacciones.dto.MovimientoDTO;
import com.nexus.ms_transacciones.dto.RespuestaTransferenciaDTO;
import com.nexus.ms_transacciones.dto.SolicitudTransferenciaDTO;
import com.nexus.ms_transacciones.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
@Tag(name = "Cliente Móvil")
public class TransaccionClienteController {

    private final TransaccionService service;

    @Operation(summary = "Iniciar Proceso de Transferencia (SAGA)", description = "Punto de entrada para la banca móvil. Realiza el débito local, comunica al Switch y maneja la compensación si hay fallos.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Procesado correctamente (Éxito o Fallo controlado)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto: Saldo insuficiente en la cuenta origen"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/transferir")
    public ResponseEntity<RespuestaTransferenciaDTO> transferir(@Valid @RequestBody SolicitudTransferenciaDTO dto) {
        RespuestaTransferenciaDTO respuesta = service.realizarTransferencia(dto);

        // Si la transacción falló, devolver HTTP 422 para que el frontend lo detecte
        if ("FAILED".equals(respuesta.getEstado())) {
            return ResponseEntity.status(422).body(respuesta);
        }
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    @Operation(summary = "Obtener movimientos por número de cuenta")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientos(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(service.obtenerMovimientosPorCuenta(numeroCuenta));
    }
}