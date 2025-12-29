package com.cuentas.productos.cbs.service;

import com.cuentas.productos.cbs.dto.CrearCuentaRequest;
import com.cuentas.productos.cbs.dto.CuentaResponse;
import com.cuentas.productos.cbs.exception.BusinessException;
import com.cuentas.productos.cbs.exception.ResourceNotFoundException;
import com.cuentas.productos.cbs.mapper.CuentaMapper;
import com.cuentas.productos.cbs.model.Cuenta;
import com.cuentas.productos.cbs.model.TipoCuenta;
import com.cuentas.productos.cbs.repository.CuentaRepository;
import com.cuentas.productos.cbs.repository.TipoCuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepo;
    private final TipoCuentaRepository tipoCuentaRepo;

    public CuentaService(CuentaRepository cuentaRepo, TipoCuentaRepository tipoCuentaRepo) {
        this.cuentaRepo = cuentaRepo;
        this.tipoCuentaRepo = tipoCuentaRepo;
    }

    @Transactional
    public CuentaResponse crear(CrearCuentaRequest req) {

        if (req.getSaldoInicial() != null && req.getSaldoInicial().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El saldo inicial no puede ser negativo");
        }

        // FIX: Validar máximo 4 cuentas por cliente
        long cantidadCuentas = cuentaRepo.findByClienteId(req.getClienteId()).size();
        if (cantidadCuentas >= 4) {
            throw new BusinessException("El cliente ya tiene el máximo permitido de 4 cuentas.");
        }

        TipoCuenta tipo = tipoCuentaRepo.findById(req.getTipoCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de cuenta no existe: " + req.getTipoCuentaId()));

        Cuenta cuenta = new Cuenta();
        cuenta.setClienteId(req.getClienteId());
        cuenta.setTipoCuenta(tipo);
        cuenta.setSucursalIdApertura(req.getSucursalIdApertura());
        cuenta.setNumeroCuenta(generarNumeroCuenta());
        cuenta.setSaldo(req.getSaldoInicial() != null ? req.getSaldoInicial() : BigDecimal.ZERO);
        cuenta.setFechaApertura(LocalDate.now());
        cuenta.setEstado("ACTIVA");

        return CuentaMapper.toResponse(cuentaRepo.save(cuenta));
    }

    public CuentaResponse obtener(Integer id) {
        Cuenta cuenta = cuentaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + id));
        return CuentaMapper.toResponse(cuenta);
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarPorCliente(Integer clienteId) {
        return cuentaRepo.findByClienteId(clienteId).stream()
                .map(c -> {
                    try {
                        return CuentaMapper.toResponse(c);
                    } catch (Exception e) {
                        // Log error but continue
                        System.err.println("Error mapping cuenta " + c.getId() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public java.util.Optional<Cuenta> buscarPorNumero(String numeroCuenta) {
        return cuentaRepo.findByNumeroCuenta(numeroCuenta);
    }

    @Transactional
    public void debitar(String numeroCuenta, BigDecimal monto) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaRepo.save(cuenta);
    }

    @Transactional
    public void acreditar(String numeroCuenta, BigDecimal monto) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepo.save(cuenta);
    }

    private String generarNumeroCuenta() {
        // Prefijo BIN: 270100 + 10 dígitos aleatorios = 16 dígitos
        String prefix = "270100";
        StringBuilder sb = new StringBuilder(prefix);
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Cambiar estado de una cuenta (ACTIVA/INACTIVA/BLOQUEADA)
     * Usado por Ventanilla para gestión administrativa
     */
    @Transactional
    public void cambiarEstado(String numeroCuenta, String nuevoEstado) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        String estadoNormalizado = nuevoEstado.toUpperCase().trim();
        if (!estadoNormalizado.equals("ACTIVA") && !estadoNormalizado.equals("INACTIVA")
                && !estadoNormalizado.equals("BLOQUEADA")) {
            throw new BusinessException("Estado no válido. Use: ACTIVA, INACTIVA o BLOQUEADA");
        }

        cuenta.setEstado(estadoNormalizado);
        cuentaRepo.save(cuenta);
    }

    /**
     * Eliminar una cuenta (solo si saldo es 0)
     * Usado por Ventanilla para gestión administrativa
     */
    @Transactional
    public void eliminarCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepo.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));

        if (cuenta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException(
                    "No se puede eliminar una cuenta con saldo. Saldo actual: " + cuenta.getSaldo());
        }

        cuentaRepo.delete(cuenta);
    }
}
