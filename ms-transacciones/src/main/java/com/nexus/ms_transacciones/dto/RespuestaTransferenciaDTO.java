package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaTransferenciaDTO {
    private Integer idTransaccion;
    private String estado;
    private String mensaje;
    private LocalDateTime fechaHora;
}