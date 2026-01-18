package com.ecusol.web.dto;
public record RegisterRequest(
    String cedula, String nombres, String apellidos, 
    String email, String usuario, String password, 
    String telefono, String direccion
) {}