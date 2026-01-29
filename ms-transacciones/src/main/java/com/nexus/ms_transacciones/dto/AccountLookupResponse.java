package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLookupResponse {

    private String status; // SUCCESS or FAILED
    private AccountData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountData {
        private Boolean exists;
        private String ownerName;
        private String currency;
        private String status;
        private String mensaje; // Para errores
    }
}
