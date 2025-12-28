package com.cuentas.productos.cbs.service;

import com.cuentas.productos.cbs.dto.TipoCuentaRequest;
import com.cuentas.productos.cbs.dto.TipoCuentaResponse;
import com.cuentas.productos.cbs.exception.ResourceNotFoundException;
import com.cuentas.productos.cbs.mapper.TipoCuentaMapper;
import com.cuentas.productos.cbs.model.TipoCuenta;
import com.cuentas.productos.cbs.repository.TipoCuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCuentaService {

    private final TipoCuentaRepository repo;

    public TipoCuentaService(TipoCuentaRepository repo) {
        this.repo = repo;
    }

    public List<TipoCuentaResponse> listar() {
        return repo.findAll().stream()
                .map(TipoCuentaMapper::toResponse)
                .toList();
    }

    public TipoCuentaResponse obtener(Integer id) {
        TipoCuenta entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no encontrado: " + id));
        return TipoCuentaMapper.toResponse(entity);
    }

    public TipoCuentaResponse crear(TipoCuentaRequest req) {
        TipoCuenta entity = TipoCuentaMapper.toEntity(req);
        entity.setId(null);
        return TipoCuentaMapper.toResponse(repo.save(entity));
    }

    public TipoCuentaResponse actualizar(Integer id, TipoCuentaRequest req) {
        TipoCuenta entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no encontrado: " + id));

        TipoCuentaMapper.updateEntity(entity, req);
        return TipoCuentaMapper.toResponse(repo.save(entity));
    }

    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de cuenta no encontrado: " + id);
        }
        repo.deleteById(id);
    }
}
