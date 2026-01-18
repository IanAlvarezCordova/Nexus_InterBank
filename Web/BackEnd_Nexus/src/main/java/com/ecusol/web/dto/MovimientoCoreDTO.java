package com.ecusol.web.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoCoreDTO {
    private Integer transaccionId;
    private String referencia;
    private String rolTransaccion; 
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fechaEjecucion;

    public String getTipo() {
        return "RECEPTOR".equals(rolTransaccion) ? "C" : "D";
    }
}