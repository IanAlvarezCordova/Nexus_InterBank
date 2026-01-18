package com.ecusol.web.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data @Builder
public class RegistroCoreRequest {
    private String cedula;
    private String nombres;
    private String apellidos;
    private String direccion;
    private String telefono;
    private LocalDate fechaNacimiento;
}