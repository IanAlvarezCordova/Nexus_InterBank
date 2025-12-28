package com.ecusol.web.controller;

import com.ecusol.web.dto.JwtResponse;
import com.ecusol.web.dto.LoginRequest;
import com.ecusol.web.dto.RegisterRequest;
import com.ecusol.web.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login/web")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        String token = authService.login(req);
        // CORRECCIÓN: Usamos .getUsuario() porque LoginRequest ahora es una clase @Data
        return ResponseEntity.ok(new JwtResponse(token, req.getUsuario()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        authService.registrar(req);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }
}