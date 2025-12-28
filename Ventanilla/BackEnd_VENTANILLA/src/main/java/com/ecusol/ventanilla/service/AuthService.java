package com.ecusol.ventanilla.service;

import com.ecusol.ventanilla.client.CoreClient;
import com.ecusol.ventanilla.config.JwtTokenProvider;
import com.ecusol.ventanilla.dto.JwtResponse;
import com.ecusol.ventanilla.dto.LoginRequest;
import com.ecusol.ventanilla.dto.SucursalDTO;
import com.ecusol.ventanilla.model.Empleado;
import com.ecusol.ventanilla.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private EmpleadoRepository empleadoRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private CoreClient coreClient; // <--- INYECTAMOS EL CLIENTE

    public JwtResponse login(LoginRequest req) {
        System.out.println(">>> LOGIN ATTEMPT: " + req.getUsuario());

        Empleado emp = empleadoRepo.findByUsuario(req.getUsuario())
                .orElseThrow(() -> {
                    System.out.println(">>> LOGIN FAILED: Usuario no encontrado");
                    return new RuntimeException("Credenciales incorrectas");
                });

        if (!passwordEncoder.matches(req.getPassword(), emp.getPasswordHash())) {
            System.out.println(">>> LOGIN FAILED: Password incorrecto");
            throw new RuntimeException("Credenciales incorrectas");
        }

        if (!Boolean.TRUE.equals(emp.getActivo())) {
            System.out.println(">>> LOGIN FAILED: Usuario inactivo");
            throw new RuntimeException("Empleado inactivo");
        }

        System.out.println(">>> LOGIN SUCCESS: Generando token");
        String token = tokenProvider.createToken(emp.getUsuario(), emp.getEmpleadoId(), emp.getRol());

        // --- LÓGICA DE NOMBRE REAL ---
        String nombreSucursal = "Sucursal Desconocida";
        try {
            SucursalDTO suc = coreClient.obtenerSucursal(emp.getSucursalId());
            if (suc != null) {
                nombreSucursal = suc.getNombre(); // Ej: "Matriz Amazonas"
            }
        } catch (Exception e) {
            System.out.println(">>> WARN: No se pudo obtener sucursal: " + e.getMessage());
            nombreSucursal = "Sucursal " + emp.getSucursalId();
        }

        return new JwtResponse(token, nombreSucursal, emp.getSucursalId());
    }
}