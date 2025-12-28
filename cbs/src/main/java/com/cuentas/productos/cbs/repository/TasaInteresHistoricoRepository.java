package com.cuentas.productos.cbs.repository;

import com.cuentas.productos.cbs.model.TasaInteresHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TasaInteresHistoricoRepository extends JpaRepository<TasaInteresHistorico, Integer> {
    List<TasaInteresHistorico> findByTipoCuenta_IdOrderByFechaInicioVigenciaDesc(Integer tipoCuentaId);
}
