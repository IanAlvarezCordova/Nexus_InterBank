package com.nexus.ms_transacciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Schema(name = "SolicitudTransferencia", description = "Cuerpo de la petición para iniciar un pago")
public class SolicitudTransferenciaDTO {

    public SolicitudTransferenciaDTO() {
    }

    @NotBlank
    @Schema(description = "Cuenta del cliente que envía los fondos", example = "2701000001")
    private String cuentaOrigen;

    @NotBlank
    @Schema(description = "Cuenta destino en el banco receptor", example = "2201000001")
    private String cuentaDestino;

    @NotNull
    @Positive
    @Schema(description = "Monto total de la transacción", example = "150.75")
    private BigDecimal monto;

    @Schema(description = "Código del banco destino para DIGICONECU (ej: BANTEC, ARCBANK, NEXUS)", example = "BANTEC")
    private String bancoDestinoCodigo;

    @Schema(description = "(Legacy) ID numérico del banco destino. 2=NEXUS (interno), otro=externo", example = "1")
    private Integer bancoDestinoId;

    @Schema(description = "Comentario o motivo del pago", example = "Pago de arriendo - Diciembre")
    private String descripcion;

    /**
     * Determina si la transferencia es interna (mismo banco NEXUS) o externa
     * (interbancaria).
     */
    public boolean esTransferenciaInterna() {
        // Es interna si:
        // 1. bancoDestinoCodigo es null o vacío (asume que es local)
        // 2. bancoDestinoCodigo es NEXUS
        // 3. bancoDestinoId es 2 (código interno para NEXUS)
        if (bancoDestinoCodigo == null || bancoDestinoCodigo.isBlank()) {
            // Si no viene código de banco, asumimos que es interna (o verificamos por ID)
            return bancoDestinoId == null || bancoDestinoId == 2;
        }
        return "NEXUS".equalsIgnoreCase(bancoDestinoCodigo) || "ECUASOL".equalsIgnoreCase(bancoDestinoCodigo);
    }
}