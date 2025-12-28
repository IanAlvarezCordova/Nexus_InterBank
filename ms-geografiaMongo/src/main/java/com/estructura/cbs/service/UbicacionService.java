package com.estructura.cbs.service;

import com.estructura.cbs.dto.UbicacionGeograficaDTO;
import com.estructura.cbs.model.UbicacionGeografica;
import com.estructura.cbs.repository.UbicacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UbicacionService {

    private static final Logger log =
            LoggerFactory.getLogger(UbicacionService.class);

    private final UbicacionRepository repository;

    public UbicacionService(UbicacionRepository repository) {
        this.repository = repository;
    }

    private UbicacionGeograficaDTO toDTO(UbicacionGeografica u) {
        UbicacionGeograficaDTO dto = new UbicacionGeograficaDTO();
        dto.setUbicacionId(u.getUbicacionId());
        dto.setNombre(u.getNombre());
        dto.setTipoUbicacion(u.getTipoUbicacion());
        dto.setUbicacionPadreId(u.getUbicacionPadreId());
        return dto;
    }

    private UbicacionGeografica toEntity(UbicacionGeograficaDTO dto) {
        UbicacionGeografica u = new UbicacionGeografica();
        u.setUbicacionId(dto.getUbicacionId());
        u.setNombre(dto.getNombre());
        u.setTipoUbicacion(dto.getTipoUbicacion());
        u.setUbicacionPadreId(dto.getUbicacionPadreId());
        return u;
    }

    public ResponseEntity<?> listar() {
        log.info("Listando todas las ubicaciones geográficas");

        List<UbicacionGeograficaDTO> lista =
                repository.findAll()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        log.info("Total de ubicaciones encontradas: {}", lista.size());
        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> listarPorPadre(Integer padreId) {
        log.info("Listando ubicaciones por ubicacionPadreId {}", padreId);

        List<UbicacionGeograficaDTO> lista =
                repository.findByUbicacionPadreId(padreId)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        log.info("Total de ubicaciones encontradas para padreId {}: {}",
                padreId, lista.size());

        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> buscarPorId(Integer id) {
        log.info("Buscando ubicación con id {}", id);

        return repository.findById(id)
                .map(u -> {
                    log.info("Ubicación encontrada con id {}", id);
                    return ResponseEntity.ok(toDTO(u));
                })
                .orElseGet(() -> {
                    log.warn("Ubicación no encontrada con id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    public ResponseEntity<?> guardar(UbicacionGeograficaDTO dto) {
        log.info("Intentando registrar ubicación");

        if (dto.getUbicacionId() == null) {
            log.warn("ubicacionId es null");
            return ResponseEntity.badRequest().body("ubicacionId es obligatorio");
        }

        if (repository.existsById(dto.getUbicacionId())) {
            log.warn("Ubicación ya existe con id {}", dto.getUbicacionId());
            return ResponseEntity.badRequest().body("Ya existe una ubicación con ese ID");
        }

        repository.save(toEntity(dto));
        log.info("Ubicación registrada correctamente con id {}", dto.getUbicacionId());

        return ResponseEntity.ok("Ubicación registrada correctamente");
    }

    public ResponseEntity<?> actualizar(Integer id, UbicacionGeograficaDTO dto) {
        log.info("Intentando actualizar ubicación con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No existe ubicación con id {}", id);
            return ResponseEntity.badRequest().body("No existe una ubicación con ese ID");
        }

        dto.setUbicacionId(id);
        repository.save(toEntity(dto));

        log.info("Ubicación actualizada correctamente con id {}", id);
        return ResponseEntity.ok("Ubicación actualizada correctamente");
    }

    public ResponseEntity<?> eliminar(Integer id) {
        log.info("Intentando eliminar ubicación con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No se puede eliminar, ubicación no existe con id {}", id);
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        log.info("Ubicación eliminada correctamente con id {}", id);

        return ResponseEntity.noContent().build();
    }
}
