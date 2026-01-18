package com.ecusol.ventanilla.dto;
import java.math.BigDecimal;
public record TransferenciaRequest(
    String cuentaOrigen, String cuentaDestino, BigDecimal monto, String descripcion
) {}