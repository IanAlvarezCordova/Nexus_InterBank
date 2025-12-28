//ubi: src/main/java/com/ecusol/ventanilla/controller/VentanillaController.java
package com.ecusol.ventanilla.controller;

import com.ecusol.ventanilla.dto.*;
import com.ecusol.ventanilla.service.VentanillaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventanilla")
@CrossOrigin(origins = "*")
public class VentanillaController {

    @Autowired private VentanillaService service;

    @GetMapping("/clientes/{cedula}")
    public ResponseEntity<ResumenClienteDTO> buscarCliente(@PathVariable String cedula) {
        return ResponseEntity.ok(service.buscarCliente(cedula));
    }

    @PostMapping("/operaciones")
    public ResponseEntity<String> operar(@RequestBody VentanillaOpDTO op) {
        String ref = service.realizarOperacion(op);
        return ResponseEntity.ok("Operación Exitosa. Ref: " + ref);
    }
    
    @GetMapping("/cuentas/validar/{numero}")
    public ResponseEntity<InfoCuentaDTO> validarCuenta(@PathVariable String numero) {
        return ResponseEntity.ok(service.validarCuenta(numero));
    }

    // --- ADMIN ---

    @PostMapping("/cuenta/estado")
    public ResponseEntity<String> cambiarEstadoCuenta(@RequestParam String cuenta, @RequestParam String estado) {
        service.cambiarEstadoCuenta(cuenta, estado);
        return ResponseEntity.ok("Estado actualizado");
    }

    @PostMapping("/activar-cuenta/{cuenta}")
    public ResponseEntity<String> activarCuenta(@PathVariable String cuenta) {
        service.activarCuenta(cuenta);
        return ResponseEntity.ok("Cuenta activada");
    }
    
    @PostMapping("/cliente/estado")
    public ResponseEntity<String> cambiarEstadoCliente(@RequestParam String cedula, @RequestParam String estado) {
        service.cambiarEstadoCliente(cedula, estado);
        return ResponseEntity.ok("Estado cliente actualizado");
    }
    
    @DeleteMapping("/cuenta/{cuenta}")
    public ResponseEntity<String> eliminarCuenta(@PathVariable String cuenta) {
        service.eliminarCuenta(cuenta);
        return ResponseEntity.ok("Cuenta eliminada");
    }
}