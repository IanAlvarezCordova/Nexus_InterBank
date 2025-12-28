package com.nexus.ms_transacciones.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTOs para el CoreVentanillaController
 */
public class VentanillaDTO {

    @Data
    public static class ResumenClienteDTO {
        private Integer clienteId;
        private String nombres;
        private String cedula;
        private String estado;
        private List<CuentaResumenDTO> cuentas;
    }

    @Data
    public static class CuentaResumenDTO {
        private String numeroCuenta;
        private String tipo;
        private Integer tipoCuentaId;
        private BigDecimal saldo;
        private String estado;
    }

    @Data
    public static class InfoCuentaDTO {
        private String numeroCuenta;
        private String nombreCompleto;
        private String tipoCuenta;
    }

    @Data
    public static class TransaccionCajaRequest {
        private String tipoOperacion; // DEPOSITO, RETIRO, TRANSFERENCIA
        private String cuentaOrigen;
        private String cuentaDestino;
        private BigDecimal monto;
        private String descripcion;
    }
}
