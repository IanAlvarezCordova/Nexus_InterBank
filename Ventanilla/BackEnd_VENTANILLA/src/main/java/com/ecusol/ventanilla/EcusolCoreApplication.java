package com.ecusol.ventanilla;

import com.ecusol.ventanilla.model.Empleado;
import com.ecusol.ventanilla.repository.EmpleadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@org.springframework.cloud.openfeign.EnableFeignClients
public class EcusolCoreApplication {

	public static void main(String[] args) {
		System.out.println(">>> APP STARTED: EcusolCoreApplication");
		SpringApplication.run(EcusolCoreApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(EmpleadoRepository empleadoRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			Empleado admin = empleadoRepo.findByUsuario("admin").orElse(new Empleado());

			admin.setUsuario("admin");
			admin.setPasswordHash(passwordEncoder.encode("admin"));
			admin.setNombres("Administrador");
			admin.setApellidos("Sistema");
			admin.setRol("ADMIN");
			admin.setActivo(true);
			admin.setSucursalId(1);

			empleadoRepo.save(admin);
			System.out.println(">>> USUARIO ADMIN ACTUALIZADO/CREADO: admin / admin");
		};
	}
}
