//ubi: src/main/java/com/ecusol/web/dto/CuentaWebDTO.java
package com.ecusol.web.dto;

import java.math.BigDecimal;

// Este es el objeto que enviamos al Frontend (React)
public record CuentaWebDTO(
        Long cuentaId,
        String numeroCuenta,
        BigDecimal saldo,
        String estado,
        Long tipoCuentaId
) {}