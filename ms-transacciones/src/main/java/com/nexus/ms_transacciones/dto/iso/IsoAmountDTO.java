package com.nexus.ms_transacciones.dto.iso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsoAmountDTO {
    private String currency; // "USD"
    private BigDecimal value;
}
