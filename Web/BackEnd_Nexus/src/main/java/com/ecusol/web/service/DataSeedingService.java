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

    // --- DATOS REALISTAS ---
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
        // 1. Generar Datos Personales
        String nombre = getRandom(NOMBRES);
        String apellido = getRandom(APELLIDOS);
        String apellido2 = getRandom(APELLIDOS); // Segundo apellido para más realismo
        String ciudad = getRandom(CIUDADES);
        String cedula = generarCedulaRealista();
        String telefono = "09" + (80000000 + random.nextInt(19999999));

        // Generar Usuario Web (Ej: SCAMILA123)
        String baseUser = (nombre.substring(0, 1) + apellido).toUpperCase();
        String username = baseUser + random.nextInt(1000); // Evitar duplicados
        String email = username.toLowerCase() + "@gmail.com";

        // 2. Llamar al Core para crear Persona
        RegistroCoreRequest coreReq = RegistroCoreRequest.builder()
                .cedula(cedula)
                .nombres(nombre + " " + getRandom(NOMBRES)) // Dos nombres
                .apellidos(apellido + " " + apellido2)
                .direccion(ciudad)
                .telefono(telefono)
                .fechaNacimiento(LocalDate.of(1970 + random.nextInt(35), 1 + random.nextInt(11), 1 + random.nextInt(28)))
                .build();

        Integer clienteId = coreClient.crearClientePersona(coreReq);

        // 3. Crear Usuario Web (Login: 1234)
        UsuarioWeb u = new UsuarioWeb();
        u.setClienteIdCore(clienteId);
        u.setUsuario(username);
        u.setPassword(passwordEncoder.encode("1234")); // Todos con clave 1234 para probar fácil
        u.setEmail(email);
        u.setEstado("ACTIVO");
        u.setFechaRegistro(java.time.LocalDateTime.now());
        usuarioRepo.save(u);

        // 4. Crear Cuentas según Distribución Probabilística
        int numCuentas = determinarNumeroDeCuentas();

        for (int j = 0; j < numCuentas; j++) {
            // Alternar entre Ahorros (1) y Corriente (2) aleatoriamente
            int tipoCuenta = random.nextBoolean() ? 1 : 2;

            // Saldo aleatorio entre $50.00 y $5,000.00
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

    // Lógica de Distribución solicitada
    private int determinarNumeroDeCuentas() {
        int r = random.nextInt(100); // 0 a 99
        if (r < 50) return 1;       // 50% tienen 1 cuenta
        else if (r < 85) return 2;  // 35% tienen 2 cuentas (Acumulado 85%)
        else if (r < 95) return 3;  // 10% tienen 3 cuentas (Acumulado 95%)
        else return 4;              // 5% tienen 4 cuentas
    }

    private String getRandom(String[] array) {
        return array[random.nextInt(array.length)];
    }

    // Genera una cédula que parece válida (empieza con código de provincia 01-24)
    private String generarCedulaRealista() {
        int provincia = 1 + random.nextInt(24);
        String provinciaStr = provincia < 10 ? "0" + provincia : String.valueOf(provincia);
        String resto = String.format("%08d", random.nextInt(99999999));
        return provinciaStr + resto; // Nota: No valida el último dígito verificador, pero parece real.
    }
}