package com.estructura.cbs.service;

import com.estructura.cbs.dto.FeriadoDTO;
import com.estructura.cbs.model.Feriado;
import com.estructura.cbs.repository.FeriadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeriadoService {

    private static final Logger log =
            LoggerFactory.getLogger(FeriadoService.class);

    private final FeriadoRepository repository;

    public FeriadoService(FeriadoRepository repository) {
        this.repository = repository;
    }

    private FeriadoDTO toDTO(Feriado f) {
        FeriadoDTO dto = new FeriadoDTO();
        dto.setFeriadoId(f.getFeriadoId());
        dto.setUbicacionId(f.getUbicacionId());
        dto.setFecha(f.getFecha());
        dto.setDescripcion(f.getDescripcion());
        return dto;
    }

    private Feriado toEntity(FeriadoDTO dto) {
        Feriado f = new Feriado();
        f.setFeriadoId(dto.getFeriadoId());
        f.setUbicacionId(dto.getUbicacionId());
        f.setFecha(dto.getFecha());
        f.setDescripcion(dto.getDescripcion());
        return f;
    }

    public ResponseEntity<?> listar() {
        log.info("Listando todos los feriados");

        List<FeriadoDTO> lista =
                repository.findAll()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        log.info("Total de feriados encontrados: {}", lista.size());
        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> buscarPorId(Integer id) {
        log.info("Buscando feriado con id {}", id);

        return repository.findById(id)
                .map(f -> {
                    log.info("Feriado encontrado con id {}", id);
                    return ResponseEntity.ok(toDTO(f));
                })
                .orElseGet(() -> {
                    log.warn("Feriado no encontrado con id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    public ResponseEntity<?> listarPorUbicacion(Integer ubicacionId) {
        log.info("Listando feriados por ubicacionId {}", ubicacionId);

        List<FeriadoDTO> lista =
                repository.findByUbicacionId(ubicacionId)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        log.info("Total de feriados encontrados para ubicacionId {}: {}",
                ubicacionId, lista.size());

        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> guardar(FeriadoDTO dto) {
        log.info("Intentando registrar feriado");

        if (dto.getFeriadoId() == null) {
            log.warn("feriadoId es null");
            return ResponseEntity.badRequest().body("feriadoId es obligatorio");
        }

        if (repository.existsById(dto.getFeriadoId())) {
            log.warn("Ya existe feriado con id {}", dto.getFeriadoId());
            return ResponseEntity.badRequest().body("Ya existe un feriado con ese ID");
        }

        repository.save(toEntity(dto));
        log.info("Feriado registrado correctamente con id {}", dto.getFeriadoId());

        return ResponseEntity.ok("Feriado registrado correctamente");
    }

    public ResponseEntity<?> actualizar(Integer id, FeriadoDTO dto) {
        log.info("Intentando actualizar feriado con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No existe feriado con id {}", id);
            return ResponseEntity.badRequest().body("No existe un feriado con ese ID");
        }

        dto.setFeriadoId(id);
        repository.save(toEntity(dto));

        log.info("Feriado actualizado correctamente con id {}", id);
        return ResponseEntity.ok("Feriado actualizado correctamente");
    }

    public ResponseEntity<?> eliminar(Integer id) {
        log.info("Intentando eliminar feriado con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No se puede eliminar, feriado no existe con id {}", id);
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        log.info("Feriado eliminado correctamente con id {}", id);

        return ResponseEntity.noContent().build();
    }
}
