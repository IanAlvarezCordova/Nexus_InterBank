package com.ecusol.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CuentaCoreDTO {
    private Integer cuentaId;
    private Integer clienteId; 
    private String numeroCuenta;
    private BigDecimal saldo;
    private String estado;
    private Integer tipoCuentaId; 
}