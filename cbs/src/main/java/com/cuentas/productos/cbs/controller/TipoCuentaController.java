package com.cuentas.productos.cbs.controller;

import com.cuentas.productos.cbs.dto.TipoCuentaRequest;
import com.cuentas.productos.cbs.dto.TipoCuentaResponse;
import com.cuentas.productos.cbs.service.TipoCuentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-cuenta")
public class TipoCuentaController {

    private final TipoCuentaService service;

    public TipoCuentaController(TipoCuentaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TipoCuentaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoCuentaResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<TipoCuentaResponse> crear(@Valid @RequestBody TipoCuentaRequest req) {
        TipoCuentaResponse creado = service.crear(req);
        URI location = URI.create("/api/tipos-cuenta/" + creado.getTipoCuentaId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoCuentaResponse> actualizar(@PathVariable Integer id,
                                                        @Valid @RequestBody TipoCuentaRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
