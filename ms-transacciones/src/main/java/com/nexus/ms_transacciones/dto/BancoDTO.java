package com.nexus.ms_transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un banco del ecosistema DIGICONECU.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BancoDTO {
    private String codigo;
    private String nombre;
}
