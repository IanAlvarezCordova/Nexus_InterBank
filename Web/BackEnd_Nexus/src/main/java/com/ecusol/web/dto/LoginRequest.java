package com.ecusol.web.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String usuario; 
    private String password;
}