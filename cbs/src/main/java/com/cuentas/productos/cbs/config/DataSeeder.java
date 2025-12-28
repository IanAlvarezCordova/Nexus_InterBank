package com.cuentas.productos.cbs.config;

import com.cuentas.productos.cbs.model.TipoCuenta;
import com.cuentas.productos.cbs.repository.TipoCuentaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataSeeder {

    @Bean
    @Transactional
    CommandLineRunner seedTiposCuenta(TipoCuentaRepository repo) {
        return args -> {
            // Si ya existen registros, no sembrar (idempotente y evita conflictos de secuencia)
            if (repo.count() > 0) {
                return;
            }

            // Tipo 1: AHORROS (Cuenta de Ahorros)
            TipoCuenta ahorros = new TipoCuenta();
            ahorros.setNombre("AHORROS");
            ahorros.setDescripcion("Cuenta de Ahorros");
            ahorros.setEstado("ACTIVO");
            ahorros.setTipoAmortizacion("MENSUAL");
            repo.save(ahorros);

            // Tipo 2: CORRIENTE (Cuenta Corriente)
            TipoCuenta corriente = new TipoCuenta();
            corriente.setNombre("CORRIENTE");
            corriente.setDescripcion("Cuenta Corriente");
            corriente.setEstado("ACTIVO");
            corriente.setTipoAmortizacion("MENSUAL");
            repo.save(corriente);
        };
    }
}
