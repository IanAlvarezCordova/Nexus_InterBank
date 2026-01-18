package com.ecusol.web.dto;

public record DestinatarioDTO(
        String numeroCuenta,
        String nombreTitular,
        String cedulaParcial,
        String tipoCuenta 
) {}