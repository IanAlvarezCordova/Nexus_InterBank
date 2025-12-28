package com.estructura.cbs.controller;

import com.estructura.cbs.dto.SucursalDTO;
import com.estructura.cbs.service.SucursalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sucursal")
public class SucursalController {

    private final SucursalService service;

    public SucursalController(SucursalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/entidad/{entidadId}")
    public ResponseEntity<?> listarPorEntidad(@PathVariable Integer entidadId) {
        return service.listarPorEntidad(entidadId);
    }

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<?> listarPorUbicacion(@PathVariable Integer ubicacionId) {
        return service.listarPorUbicacion(ubicacionId);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody SucursalDTO dto) {
        return service.guardar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id,
                                        @RequestBody SucursalDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return service.eliminar(id);
    }
}
