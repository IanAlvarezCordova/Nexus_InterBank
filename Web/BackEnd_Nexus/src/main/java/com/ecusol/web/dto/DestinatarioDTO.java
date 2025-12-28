//ubi: src/main/java/com/ecusol/web/dto/DestinatarioDTO.java
package com.ecusol.web.dto;

public record DestinatarioDTO(
        String numeroCuenta,
        String nombreTitular,
        String cedulaParcial,
        String tipoCuenta // <--- CAMPO NUEVO OBLIGATORIO
) {}