package com.ecusol.web.service;

import com.ecusol.web.client.CoreBancarioClient;
import com.ecusol.web.dto.CrearCuentaRequest;
import com.ecusol.web.dto.RegistroCoreRequest;
import com.ecusol.web.model.UsuarioWeb;
import com.ecusol.web.repository.UsuarioWebRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class DataSeedingService {

    @Autowired private CoreBancarioClient coreClient;
    @Autowired private UsuarioWebRepository usuarioRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    private final String[] NOMBRES = {
            "Santiago", "Mateo", "Sebastián", "Alejandro", "Matías", "Diego", "Samuel", "Nicolás", "Daniel", "Leonardo",
            "Valentina", "Sofía", "Camila", "Daniela", "Valeria", "María", "Paula", "Isabella", "Gabriela", "Sara",
            "Carlos", "Luis", "Jorge", "Miguel", "José", "Ana", "Lucía", "Carmen", "Elena", "Marta"
    };

    private final String[] APELLIDOS = {
            "Zambrano", "Sánchez", "Torres", "Rodríguez", "López", "González", "García", "Morales", "Pérez", "Castillo",
            "Romero", "Salazar", "Molina", "Castro", "Ortiz", "Vargas", "Guerrero", "Rojas", "Delgado", "Chávez",
            "Ibarra", "Mejía", "Vera", "Cedeño", "Macías", "Alvarado", "Bustamante", "Paredes", "Villavicencio", "Espinosa"
    };

    private final String[] CIUDADES = {
            "Quito, Sector Norte", "Quito, Valle de los Chillos", "Guayaquil, Samborondón", "Guayaquil, Ceibos",
            "Cuenca, Centro Histórico", "Ambato, Ficoa", "Manta, Barbasquillo", "Loja, San Sebastián", "Riobamba", "Ibarra"
    };

    public String poblarBaseDeDatos(int cantidad) {
        int creados = 0;
        int errores = 0;

        for (int i = 0; i < cantidad; i++) {
            try {
                crearClienteCompleto();
                creados++;
            } catch (Exception e) {
                System.err.println("Error creando cliente dummy: " + e.getMessage());
                errores++;
            }
        }
        return "Proceso finalizado. Clientes creados: " + creados + ". Errores: " + errores;
    }

    private void crearClienteCompleto() {
        String nombre = getRandom(NOMBRES);
        String apellido = getRandom(APELLIDOS);
        String apellido2 = getRandom(APELLIDOS);
        String ciudad = getRandom(CIUDADES);
        String cedula = generarCedulaRealista();
        String telefono = "09" + (80000000 + random.nextInt(19999999));

        String baseUser = (nombre.substring(0, 1) + apellido).toUpperCase();
        String username = baseUser + random.nextInt(1000);
        String email = username.toLowerCase() + "@gmail.com";

        RegistroCoreRequest coreReq = RegistroCoreRequest.builder()
                .cedula(cedula)
                .nombres(nombre + " " + getRandom(NOMBRES)) 
                .apellidos(apellido + " " + apellido2)
                .direccion(ciudad)
                .telefono(telefono)
                .fechaNacimiento(LocalDate.of(1970 + random.nextInt(35), 1 + random.nextInt(11), 1 + random.nextInt(28)))
                .build();

        Integer clienteId = coreClient.crearClientePersona(coreReq);

        UsuarioWeb u = new UsuarioWeb();
        u.setClienteIdCore(clienteId);
        u.setUsuario(username);
        u.setPassword(passwordEncoder.encode("1234")); 
        u.setEmail(email);
        u.setEstado("ACTIVO");
        u.setFechaRegistro(java.time.LocalDateTime.now());
        usuarioRepo.save(u);

        int numCuentas = determinarNumeroDeCuentas();

        for (int j = 0; j < numCuentas; j++) {
            int tipoCuenta = random.nextBoolean() ? 1 : 2;

            double saldo = 50 + (5000 - 50) * random.nextDouble();
            BigDecimal saldoBD = BigDecimal.valueOf(saldo).setScale(2, java.math.RoundingMode.HALF_UP);

            CrearCuentaRequest cuentaReq = CrearCuentaRequest.builder()
                    .clienteId(clienteId)
                    .tipoCuentaId(tipoCuenta)
                    .saldoInicial(saldoBD)
                    .build();

            coreClient.crearCuenta(cuentaReq);
        }

        System.out.println("--> Cliente creado: " + username + " con " + numCuentas + " cuentas.");
    }

    private int determinarNumeroDeCuentas() {
        int r = random.nextInt(100); 
        if (r < 50) return 1;      
        else if (r < 85) return 2;  
        else if (r < 95) return 3;  
        else return 4;              
    }

    private String getRandom(String[] array) {
        return array[random.nextInt(array.length)];
    }

    private String generarCedulaRealista() {
        int provincia = 1 + random.nextInt(24);
        String provinciaStr = provincia < 10 ? "0" + provincia : String.valueOf(provincia);
        String resto = String.format("%08d", random.nextInt(99999999));
        return provinciaStr + resto; 
    }
}