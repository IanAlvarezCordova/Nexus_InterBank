package com.nexus.ms_transacciones.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IsoMensajeDTO implements Serializable {

    @JsonProperty("header")
    private IsoHeader header;

    @JsonProperty("body")
    private IsoBody body;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsoHeader implements Serializable {
        @JsonProperty("messageId")
        private String messageId;
        @JsonProperty("creationDateTime")
        private String creationDateTime;
        @JsonProperty("originatingBankId")
        private String originatingBankId;
        @JsonProperty("callbackUrl")
        private String callbackUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsoBody implements Serializable {
        @JsonProperty("instructionId")
        private String instructionId;
        @JsonProperty("endToEndId")
        private String endToEndId;
        @JsonProperty("amount")
        private IsoAmount amount;
        @JsonProperty("debtor")
        private IsoDebtor debtor;
        @JsonProperty("creditor")
        private IsoCreditor creditor;
        @JsonProperty("remittanceInformation")
        private String remittanceInformation;
        @JsonProperty("originalInstructionId")
        private String originalInstructionId;
        @JsonProperty("returnReason")
        private String returnReason;
        @JsonProperty("returnAmount")
        private IsoAmount returnAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsoAmount implements Serializable {
        @JsonProperty("currency")
        private String currency;
        @JsonProperty("value")
        private BigDecimal value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsoDebtor implements Serializable {
        @JsonProperty("name")
        private String name;
        @JsonProperty("accountId")
        private String accountId;
        @JsonProperty("accountType")
        private String accountType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IsoCreditor implements Serializable {
        @JsonProperty("name")
        private String name;
        @JsonProperty("accountId")
        private String accountId;
        @JsonProperty("accountType")
        private String accountType;
        @JsonProperty("targetBankId")
        private String targetBankId;
    }
}