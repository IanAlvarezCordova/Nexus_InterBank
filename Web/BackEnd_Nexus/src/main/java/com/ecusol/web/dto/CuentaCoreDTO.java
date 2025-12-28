//ubi: src/main/java/com/ecusol/web/dto/CuentaCoreDTO.java
package com.ecusol.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CuentaCoreDTO {
    private Integer cuentaId;
    private Integer clienteId; // FIX: Necessary for validation
    private String numeroCuenta;
    private BigDecimal saldo;
    private String estado;
    private Integer tipoCuentaId; // Ahora sí llegará poblado correctamente desde el Core
}