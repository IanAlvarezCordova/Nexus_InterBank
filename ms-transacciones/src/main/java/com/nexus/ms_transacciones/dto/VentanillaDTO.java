package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class VentanillaDTO {

    private VentanillaDTO() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenClienteDTO implements Serializable {
        private Integer clienteId;
        private String nombres;
        private String cedula;
        private String estado;
        private List<CuentaResumenDTO> cuentas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaResumenDTO implements Serializable {
        private String numeroCuenta;
        private BigDecimal saldo;
        private String estado;
        private String tipo;
        private Integer tipoCuentaId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoCuentaDTO implements Serializable {
        private String numeroCuenta;
        private String nombreCompleto;
        private String tipoCuenta;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransaccionCajaRequest implements Serializable {

        private String tipoOperacion;      
        private BigDecimal monto;        
        private String cuentaOrigen;        
        private String cuentaDestino;         
        private String descripcion;
    }
}