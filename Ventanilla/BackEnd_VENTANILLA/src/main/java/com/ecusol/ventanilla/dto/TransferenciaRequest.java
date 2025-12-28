//ubi: src/main/java/com/ecusol/ventanilla/dto/TransferenciaRequest.java
package com.ecusol.ventanilla.dto;
import java.math.BigDecimal;
public record TransferenciaRequest(
    String cuentaOrigen, String cuentaDestino, BigDecimal monto, String descripcion
) {}