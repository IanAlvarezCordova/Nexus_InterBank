package com.estructura.cbs.controller;

import com.estructura.cbs.dto.FeriadoDTO;
import com.estructura.cbs.service.FeriadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feriado")
public class FeriadoController {

    private final FeriadoService service;

    public FeriadoController(FeriadoService service) {
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

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<?> listarPorUbicacion(@PathVariable Integer ubicacionId) {
        return service.listarPorUbicacion(ubicacionId);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody FeriadoDTO dto) {
        return service.guardar(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id,
                                        @RequestBody FeriadoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return service.eliminar(id);
    }
}
