package com.cuentas.productos.cbs.controller;

import com.cuentas.productos.cbs.dto.CrearCuentaRequest;
import com.cuentas.productos.cbs.dto.CuentaResponse;
import com.cuentas.productos.cbs.service.CuentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CrearCuentaRequest req) {
        CuentaResponse creada = service.crear(req);
        URI location = URI.create("/api/cuentas/" + creada.getCuentaId());
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listarPorCliente(@RequestParam Integer clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @GetMapping("/por-numero/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> buscarPorNumero(@PathVariable String numeroCuenta) {
        return service.buscarPorNumero(numeroCuenta)
                .map(com.cuentas.productos.cbs.mapper.CuentaMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cambiar estado de una cuenta (ACTIVA/INACTIVA/BLOQUEADA)
     * Usado por Ventanilla para gestión administrativa
     */
    @PutMapping("/{numeroCuenta}/estado")
    public ResponseEntity<String> cambiarEstado(
            @PathVariable String numeroCuenta,
            @RequestParam String estado) {
        service.cambiarEstado(numeroCuenta, estado);
        return ResponseEntity.ok("Estado de cuenta actualizado a " + estado);
    }

    /**
     * Eliminar una cuenta (solo si saldo es 0)
     * Usado por Ventanilla para gestión administrativa
     */
    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<String> eliminarCuenta(@PathVariable String numeroCuenta) {
        service.eliminarCuenta(numeroCuenta);
        return ResponseEntity.ok("Cuenta eliminada correctamente");
    }

    // ========== ENDPOINTS PARA TRANSACCIONES (ms-transacciones) ==========

    /**
     * Débito de cuenta (restar saldo)
     * Usado por ms-transacciones para transferencias salientes
     */
    @PostMapping("/debito")
    public ResponseEntity<String> debitar(@RequestBody DebitoRequest request) {
        service.debitar(request.cuenta(), request.monto());
        return ResponseEntity.ok("Débito exitoso");
    }

    /**
     * Crédito de cuenta (sumar saldo)
     * Usado por ms-transacciones para transferencias entrantes
     */
    @PostMapping("/credito")
    public ResponseEntity<String> acreditar(@RequestBody DebitoRequest request) {
        service.acreditar(request.cuenta(), request.monto());
        return ResponseEntity.ok("Crédito exitoso");
    }

    // DTO interno para las operaciones de débito/crédito
    public record DebitoRequest(String cuenta, java.math.BigDecimal monto) {
    }
}
