package com.nexus.ms_clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {
    private Integer clienteId;
    private String nombres;
    private String apellidos;
    private String numeroIdentificacion;
    private String email;
    private String estado;
}
