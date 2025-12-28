package com.nexus.ms_clientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegistroClientePersonaDTO {

    @NotBlank(message = "nombres es obligatorio")
    @Size(max = 100, message = "nombres no debe exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "apellidos es obligatorio")
    @Size(max = 100, message = "apellidos no debe exceder 100 caracteres")
    private String apellidos;

    @NotBlank(message = "cedula es obligatorio")
    @Size(min = 10, max = 13, message = "cedula debe tener entre 10 y 13 caracteres")
    private String cedula;

    @NotBlank(message = "direccion es obligatorio")
    @Size(max = 200, message = "direccion no debe exceder 200 caracteres")
    private String direccion;

    @NotNull(message = "fechaNacimiento es obligatorio")
    @Past(message = "fechaNacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;
}