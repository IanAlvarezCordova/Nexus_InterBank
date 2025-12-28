package com.ecusol.web.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoCoreDTO {
    private Integer transaccionId;
    private String referencia;
    private String rolTransaccion; // "EMISOR" o "RECEPTOR"
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fechaEjecucion; // Ojo: En el core se llama fechaEjecucion

    // Helper para convertir a 'C' (Crédito/Verde) o 'D' (Débito/Rojo)
    public String getTipo() {
        return "RECEPTOR".equals(rolTransaccion) ? "C" : "D";
    }
}