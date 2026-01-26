package com.nexus.ms_transacciones.repository;

import com.nexus.ms_transacciones.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    boolean existsByInstructionId(String instructionId);

    Optional<Transaccion> findByInstructionId(String instructionId);

    List<Transaccion> findAllByCuentaOrigenOrCuentaDestinoOrderByFechaEjecucionDesc(String cuentaOrigen,
            String cuentaDestino);

}