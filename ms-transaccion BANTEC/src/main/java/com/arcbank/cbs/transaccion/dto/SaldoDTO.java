package com.arcbank.cbs.transaccion.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoDTO {
    private BigDecimal saldo;
}
