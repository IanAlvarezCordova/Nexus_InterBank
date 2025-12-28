package com.estructura.cbs.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FeriadoDTO {

    private Integer feriadoId;
    private Integer ubicacionId;
    private LocalDate fecha;
    private String descripcion;

    public FeriadoDTO() {}

    public FeriadoDTO(Integer feriadoId) {
        this.feriadoId = feriadoId;
    }
}
