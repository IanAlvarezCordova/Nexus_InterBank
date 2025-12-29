package com.nexus.ms_transacciones.dto.iso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsoAccountDTO {
    private String name;
    private String accountId;
    private String accountType; // CHECKING, SAVINGS
    private String targetBankId; // Opcional (solo para creditor)
}
