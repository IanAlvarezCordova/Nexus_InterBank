package com.estructura.cbs.service;

import com.estructura.cbs.dto.SucursalDTO;
import com.estructura.cbs.model.Sucursal;
import com.estructura.cbs.repository.SucursalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalService {

    private static final Logger log =
            LoggerFactory.getLogger(SucursalService.class);

    private final SucursalRepository repository;

    public SucursalService(SucursalRepository repository) {
        this.repository = repository;
    }

    private SucursalDTO toDTO(Sucursal s) {
        SucursalDTO dto = new SucursalDTO();
        dto.setSucursalId(s.getSucursalId());
        dto.setEntidadId(s.getEntidadId());
        dto.setUbicacionId(s.getUbicacionId());
        dto.setCodigoSucursal(s.getCodigoSucursal());
        dto.setNombre(s.getNombre());
        dto.setDireccion(s.getDireccion());
        dto.setTelefono(s.getTelefono());
        dto.setLatitud(s.getLatitud());
        dto.setLongitud(s.getLongitud());
        dto.setEstado(s.getEstado());
        return dto;
    }

    private Sucursal toEntity(SucursalDTO dto) {
        Sucursal s = new Sucursal();
        s.setSucursalId(dto.getSucursalId());
        s.setEntidadId(dto.getEntidadId());
        s.setUbicacionId(dto.getUbicacionId());
        s.setCodigoSucursal(dto.getCodigoSucursal());
        s.setNombre(dto.getNombre());
        s.setDireccion(dto.getDireccion());
        s.setTelefono(dto.getTelefono());
        s.setLatitud(dto.getLatitud());
        s.setLongitud(dto.getLongitud());
        s.setEstado(dto.getEstado());
        return s;
    }

    public ResponseEntity<?> listar() {
        log.info("Listando todas las sucursales");

        List<SucursalDTO> lista = repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        log.info("Total de sucursales encontradas: {}", lista.size());
        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> buscarPorId(Integer id) {
        log.info("Buscando sucursal con id {}", id);

        return repository.findById(id)
                .map(s -> {
                    log.info("Sucursal encontrada con id {}", id);
                    return ResponseEntity.ok(toDTO(s));
                })
                .orElseGet(() -> {
                    log.warn("Sucursal no encontrada con id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    public ResponseEntity<?> listarPorEntidad(Integer entidadId) {
        log.info("Listando sucursales por entidadId {}", entidadId);

        List<SucursalDTO> lista =
                repository.findByEntidadId(entidadId)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        log.info("Total de sucursales encontradas para entidadId {}: {}",
                entidadId, lista.size());

        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> listarPorUbicacion(Integer ubicacionId) {
        log.info("Listando sucursales por ubicacionId {}", ubicacionId);

        List<SucursalDTO> lista =
                repository.findByUbicacionId(ubicacionId)
                        .stream()
                        .map(this::toDTO)
                        .toList();

        log.info("Total de sucursales encontradas para ubicacionId {}: {}",
                ubicacionId, lista.size());

        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<?> guardar(SucursalDTO dto) {
        log.info("Intentando registrar sucursal");

        if (dto.getSucursalId() == null) {
            log.warn("sucursalId es null");
            return ResponseEntity.badRequest().body("sucursalId es obligatorio");
        }

        if (repository.existsById(dto.getSucursalId())) {
            log.warn("Sucursal ya existe con id {}", dto.getSucursalId());
            return ResponseEntity.badRequest().body("Ya existe una sucursal con ese ID");
        }

        repository.save(toEntity(dto));
        log.info("Sucursal registrada correctamente con id {}", dto.getSucursalId());

        return ResponseEntity.ok("Sucursal registrada correctamente");
    }

    public ResponseEntity<?> actualizar(Integer id, SucursalDTO dto) {
        log.info("Intentando actualizar sucursal con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No existe sucursal con id {}", id);
            return ResponseEntity.badRequest().body("No existe una sucursal con ese ID");
        }

        dto.setSucursalId(id);
        repository.save(toEntity(dto));

        log.info("Sucursal actualizada correctamente con id {}", id);
        return ResponseEntity.ok("Sucursal actualizada correctamente");
    }

    public ResponseEntity<?> eliminar(Integer id) {
        log.info("Intentando eliminar sucursal con id {}", id);

        if (!repository.existsById(id)) {
            log.warn("No se puede eliminar, sucursal no existe con id {}", id);
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        log.info("Sucursal eliminada correctamente con id {}", id);

        return ResponseEntity.noContent().build();
    }
}
