package com.ecusol.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {
    private Integer clienteId;
    private String nombres;
    private String apellidos;
    private String numeroIdentificacion;
    private String email;
}
