package com.estructura.cbs.config;

import com.estructura.cbs.model.Sucursal;
import com.estructura.cbs.repository.SucursalRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MongoDataSeeder {

    @Bean
    CommandLineRunner start(SucursalRepository repository, MongoTemplate mongoTemplate) {
        return args -> {
            // Verificamos si ya existen datos para no duplicar
            if (repository.count() > 0) {
                return;
            }

            // Datos reales de sucursales en Ecuador
            List<Sucursal> sucursales = Arrays.asList(
                    crearSucursal(1, 1, 1, "MATRIZ", "Matriz Quito", "Av. Amazonas y Naciones Unidas", "022567890",
                            -0.180653, -78.467834),
                    crearSucursal(2, 1, 1, "UIO-CNTR", "Quito Centro Histórico", "Calle Guayaquil y Espejo",
                            "022289123", -0.218567, -78.513426),
                    crearSucursal(3, 1, 1, "UIO-NOR", "Quito Norte - Condado", "Av. de la Prensa y Mariscal Sucre",
                            "022490567", -0.111234, -78.490123),
                    crearSucursal(4, 1, 2, "GYE-CEN", "Guayaquil Centro", "Av. 9 de Octubre y Chile", "042321456",
                            -2.190123, -79.882345),
                    crearSucursal(5, 1, 2, "GYE-NOR", "Guayaquil Norte - Mall del Sol", "Av. Juan Tanca Marengo",
                            "042678901", -2.156789, -79.894567),
                    crearSucursal(6, 1, 3, "CUE-CEN", "Cuenca Centro", "Calle Gran Colombia y Benigno Malo",
                            "072890123", -2.900123, -79.005678),
                    crearSucursal(7, 1, 4, "MAN-TAR", "Manta - Tarqui", "Calle 13 y Avenida 24", "052678901", -0.956789,
                            -80.712345),
                    crearSucursal(8, 1, 5, "AMB-CEN", "Ambato Centro", "Calle Bolívar y Montalvo", "032456789",
                            -1.245678, -78.623456));

            repository.saveAll(sucursales);
            System.out.println(">>> SEEDING COMPLETADO: 8 Sucursales de Ecuador creadas.");
        };
    }

    private Sucursal crearSucursal(Integer sucursalId, Integer entidadId, Integer ubicacionId, String codigo,
            String nombre, String direccion, String telefono, double lat, double lon) {
        Sucursal s = new Sucursal();
        s.setSucursalId(sucursalId);
        s.setEntidadId(entidadId);
        s.setUbicacionId(ubicacionId);
        s.setCodigoSucursal(codigo);
        s.setNombre(nombre);
        s.setDireccion(direccion);
        s.setTelefono(telefono);
        s.setLatitud(BigDecimal.valueOf(lat));
        s.setLongitud(BigDecimal.valueOf(lon));
        s.setEstado("ACTIVO");
        return s;
    }
}
