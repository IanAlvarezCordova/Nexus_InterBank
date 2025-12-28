package com.ecusol.web.service;

import com.ecusol.web.config.JwtTokenProvider;
import com.ecusol.web.dto.LoginRequest;
import com.ecusol.web.dto.RegisterRequest;
import com.ecusol.web.dto.RegistroCoreRequest;
import com.ecusol.web.model.UsuarioWeb;
import com.ecusol.web.repository.UsuarioWebRepository;
import com.ecusol.web.client.CoreBancarioClient; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired private UsuarioWebRepository usuarioRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider tokenProvider;
    @Autowired private CoreBancarioClient coreBancarioClient; // Inyectar

    public String login(LoginRequest req) {
        // Nota: req.getUsuario() o req.usuario() según tu DTO. Asumimos clase Lombok.
        UsuarioWeb user = usuarioRepo.findByUsuario(req.getUsuario())
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // 1. Validación Local (Usuario Web)
        if (!"ACTIVO".equals(user.getEstado())) {
            throw new RuntimeException("Usuario Web inactivo. Contacte al banco.");
        }

        // 2. Validación Remota (Cliente Core) - BLOQUEO DE SEGURIDAD
        try {
            if (!coreBancarioClient.isClienteActivo(user.getClienteIdCore())) {
                throw new RuntimeException("SU CLIENTE BANCARIO ESTÁ INACTIVO/BLOQUEADO. Por favor acérquese a una agencia.");
            }
        } catch (Exception e) {
            // Si el error es nuestro mensaje de bloqueo, lo relanzamos
            if (e.getMessage().contains("INACTIVO")) throw e;
            // Si es error de red, puedes decidir si bloquear o loguear warning.
            // Por seguridad bancaria, si no puedo verificar estado, bloqueo.
            throw new RuntimeException("Error verificando estado bancario. Intente más tarde.");
        }

        user.setUltimoAcceso(LocalDateTime.now());
        usuarioRepo.save(user);

        return tokenProvider.createToken(
                user.getUsuario(),
                user.getUsuarioWebId(),
                user.getClienteIdCore()
        );
    }

    public void registrar(RegisterRequest req) {
        if (usuarioRepo.existsByUsuario(req.usuario())) {
            throw new RuntimeException("El usuario ya existe");
        }

        RegistroCoreRequest coreReq = RegistroCoreRequest.builder()
                .cedula(req.cedula())
                .nombres(req.nombres())
                .apellidos(req.apellidos())
                .direccion(req.direccion())
                .telefono(req.telefono())
                .fechaNacimiento(java.time.LocalDate.of(2000, 1, 1))
                .build();

        Integer idCoreGenerado;
        try {
            idCoreGenerado = coreBancarioClient.crearClientePersona(coreReq);
        } catch (Exception e) {
            throw new RuntimeException("Error creando cliente en el Core: " + e.getMessage());
        }

        UsuarioWeb u = new UsuarioWeb();
        u.setUsuario(req.usuario());
        u.setPassword(passwordEncoder.encode(req.password()));
        u.setEmail(req.email());
        u.setClienteIdCore(idCoreGenerado);
        u.setEstado("ACTIVO");

        usuarioRepo.save(u);
    }
}