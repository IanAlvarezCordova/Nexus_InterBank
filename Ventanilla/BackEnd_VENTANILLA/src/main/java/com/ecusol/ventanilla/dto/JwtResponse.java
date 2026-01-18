package com.ecusol.ventanilla.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class JwtResponse {
    private String token;
    private String nombreSucursal;
    private Integer sucursalId;
}