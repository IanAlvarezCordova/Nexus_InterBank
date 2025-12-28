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
            // Tipo 1: Cuenta de Ahorros (AHORROS)
            if (!repo.existsByNombre("Cuenta de Ahorros")) {
                TipoCuenta ahorros = new TipoCuenta();
                ahorros.setNombre("Cuenta de Ahorros");
                ahorros.setDescripcion("AHORROS");
                ahorros.setEstado("ACTIVO");
                ahorros.setTipoAmortizacion("MENSUAL");
                repo.save(ahorros);
            }

            // Tipo 2: Cuenta Corriente (CORRIENTE)
            if (!repo.existsByNombre("Cuenta Corriente")) {
                TipoCuenta corriente = new TipoCuenta();
                corriente.setNombre("Cuenta Corriente");
                corriente.setDescripcion("CORRIENTE");
                corriente.setEstado("ACTIVO");
                corriente.setTipoAmortizacion("MENSUAL");
                repo.save(corriente);
            }
        };
    }
}
