package com.nexus.ms_transacciones.dto.iso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsoHeaderDTO {
    private String messageId;
    private String creationDateTime;
    private String originatingBankId;
}
