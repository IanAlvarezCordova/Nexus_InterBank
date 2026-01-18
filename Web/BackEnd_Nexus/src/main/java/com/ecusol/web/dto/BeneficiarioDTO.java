package com.ecusol.web.dto;

public record BeneficiarioDTO(
        Integer id,
        String numeroCuenta,
        String nombreTitular,
        String alias,
        String tipoCuenta 
) {}