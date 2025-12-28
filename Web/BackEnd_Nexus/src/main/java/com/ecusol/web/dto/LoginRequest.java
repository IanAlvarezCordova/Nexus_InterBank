package com.ecusol.web.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String usuario;  // "usuario" para coincidir con el JSON del front
    private String password;
}