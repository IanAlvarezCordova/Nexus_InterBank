package com.ecusol.web.dto;

import java.math.BigDecimal;

public record CuentaWebDTO(
        Long cuentaId,
        String numeroCuenta,
        BigDecimal saldo,
        String estado,
        Long tipoCuentaId
) {}