//ubi: src/main/java/com/ecusol/web/dto/MovimientoWebDTO.java
package com.ecusol.web.dto;
import java.time.LocalDateTime;

public record MovimientoWebDTO(
    LocalDateTime fecha,
    String tipo,
    Double monto,
    Double saldoNuevo,
    String descripcion,
    String operacion
) {}
