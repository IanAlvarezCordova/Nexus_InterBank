package com.cuentas.productos.cbs.controller;

import com.cuentas.productos.cbs.dto.TasaInteresRequest;
import com.cuentas.productos.cbs.dto.TasaInteresResponse;
import com.cuentas.productos.cbs.service.TasaInteresService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasas")
public class TasaInteresController {

    private final TasaInteresService service;

    public TasaInteresController(TasaInteresService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TasaInteresResponse>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/tipo/{tipoCuentaId}")
    public ResponseEntity<List<TasaInteresResponse>> listarPorTipoCuenta(@PathVariable Integer tipoCuentaId) {
        return ResponseEntity.ok(service.listarPorTipoCuenta(tipoCuentaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TasaInteresResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping("/tipo/{tipoCuentaId}")
    public ResponseEntity<TasaInteresResponse> crear(@PathVariable Integer tipoCuentaId,
                                                     @Valid @RequestBody TasaInteresRequest req) {
        TasaInteresResponse creada = service.crear(tipoCuentaId, req);
        URI location = URI.create("/api/tasas/" + creada.getTasaInteresHistoricoId());
        return ResponseEntity.created(location).body(creada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
