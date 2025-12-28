package com.estructura.cbs.service;

import com.estructura.cbs.dto.EntidadDTO;
import com.estructura.cbs.model.Entidad;
import com.estructura.cbs.repository.EntidadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntidadService {

    private static final Logger log =
            LoggerFactory.getLogger(EntidadService.class);

    private final EntidadRepository repository;

    public EntidadService(EntidadRepository repository) {
        this.repository = repository;
    }

    private EntidadDTO toDTO(Entidad e) {
        EntidadDTO dto = new EntidadDTO();
        dto.setEntidadId(e.getEntidadId());
        dto.setRuc(e.getRuc());
        dto.setRazonSocial(e.getRazonSocial());
        dto.setNombreComercial(e.getNombreComercial());
        dto.setEstado(e.getEstado());
        return dto;
    }

    private Entidad toEntity(EntidadDTO dto) {
        Entidad e = new Entidad();
        e.setEntidadId(dto.getEntidadId());
        e.setRuc(dto.getRuc());
        e.setRazonSocial(dto.getRazonSocial());
        e.setNombreComercial(dto.getNombreComercial());
        e.setEstado(dto.getEstado());
        return e;
    }

    public ResponseEntity<?> listar() {
        log.info("Listando todas las entidades");

        List<EntidadDTO> lista = repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        log.info("Total de entidades encontradas: {}", lista.size());
        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> buscarPorId(Integer id) {
        log.info("Buscando entidad con id {}", id);

        return repository.findById(id)
                .map(e -> {
                    log.info("Entidad encontrada con id {}", id);
                    return ResponseEntity.ok(toDTO(e));
                })
                .orElseGet(() -> {
                    log.warn("Entidad no encontrada con id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    public ResponseEntity<?> guardar(EntidadDTO dto) {
        log.info("Intentando registrar entidad");

        if (dto.getEntidadId() == null) {
            log.warn("entidadId es null");
            return ResponseEntity.badRequest().body("entidadId es obligatorio");
        }

        if (repository.existsById(dto.getEntidadId())) {
            log.warn("Entidad ya existe con id {}", dto.getEntidadId());
            return ResponseEntity.badRequest().body("Ya existe una entidad con ese ID");
        }

        if (repository.existsByRuc(dto.getRuc())) {
            log.warn("Entidad ya existe con RUC {}", dto.getRuc());
            return ResponseEntity.badRequest().body("Ya existe una entidad con ese RUC");
        }

        repository.save(toEntity(dto));
        log.info("Entidad registrada correctamente con id {}", dto.getEntidadId());

        return ResponseEntity.ok("Entidad registrada correctamente");
    }

    public ResponseEntity<?> actualizar(Integer id, EntidadDTO dto) {
        log.info("Intentando actualizar entidad con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No existe entidad con id {}", id);
            return ResponseEntity.badRequest().body("No existe una entidad con ese ID");
        }

        dto.setEntidadId(id);
        repository.save(toEntity(dto));

        log.info("Entidad actualizada correctamente con id {}", id);
        return ResponseEntity.ok("Entidad actualizada correctamente");
    }

    public ResponseEntity<?> eliminar(Integer id) {
        log.info("Intentando eliminar entidad con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No se puede eliminar, entidad no existe con id {}", id);
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        log.info("Entidad eliminada correctamente con id {}", id);

        return ResponseEntity.noContent().build();
    }
}
