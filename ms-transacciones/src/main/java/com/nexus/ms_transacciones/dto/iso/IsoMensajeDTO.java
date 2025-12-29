package com.nexus.ms_transacciones.dto.iso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsoMensajeDTO {
    private IsoHeaderDTO header;
    private IsoBodyDTO body;
}
