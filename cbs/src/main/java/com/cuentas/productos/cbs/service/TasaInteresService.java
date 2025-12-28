package com.cuentas.productos.cbs.service;

import com.cuentas.productos.cbs.dto.TasaInteresRequest;
import com.cuentas.productos.cbs.dto.TasaInteresResponse;
import com.cuentas.productos.cbs.exception.ResourceNotFoundException;
import com.cuentas.productos.cbs.mapper.TasaInteresMapper;
import com.cuentas.productos.cbs.model.TasaInteresHistorico;
import com.cuentas.productos.cbs.model.TipoCuenta;
import com.cuentas.productos.cbs.repository.TasaInteresHistoricoRepository;
import com.cuentas.productos.cbs.repository.TipoCuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TasaInteresService {

    private final TasaInteresHistoricoRepository repo;
    private final TipoCuentaRepository tipoCuentaRepo;

    public TasaInteresService(TasaInteresHistoricoRepository repo, TipoCuentaRepository tipoCuentaRepo) {
        this.repo = repo;
        this.tipoCuentaRepo = tipoCuentaRepo;
    }

    public List<TasaInteresResponse> listarTodas() {
        return repo.findAll().stream()
                .map(TasaInteresMapper::toResponse)
                .toList();
    }

    public List<TasaInteresResponse> listarPorTipoCuenta(Integer tipoCuentaId) {
        return repo.findByTipoCuenta_IdOrderByFechaInicioVigenciaDesc(tipoCuentaId).stream()
                .map(TasaInteresMapper::toResponse)
                .toList();
    }

    public TasaInteresResponse obtener(Integer id) {
        TasaInteresHistorico entity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tasa no encontrada: " + id));
        return TasaInteresMapper.toResponse(entity);
    }

    public TasaInteresResponse crear(Integer tipoCuentaId, TasaInteresRequest req) {
        TipoCuenta tipo = tipoCuentaRepo.findById(tipoCuentaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no encontrado: " + tipoCuentaId));

        TasaInteresHistorico entity = TasaInteresMapper.toEntity(req);
        entity.setId(null);
        entity.setTipoCuenta(tipo);

        return TasaInteresMapper.toResponse(repo.save(entity));
    }

    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Tasa no encontrada: " + id);
        }
        repo.deleteById(id);
    }
}
