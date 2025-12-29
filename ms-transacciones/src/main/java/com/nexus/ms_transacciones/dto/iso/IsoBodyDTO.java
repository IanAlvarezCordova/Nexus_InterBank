package com.nexus.ms_transacciones.dto.iso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsoBodyDTO {
    private String instructionId;
    private String endToEndId;
    private IsoAmountDTO amount;
    private IsoAccountDTO debtor;
    private IsoAccountDTO creditor;
}
