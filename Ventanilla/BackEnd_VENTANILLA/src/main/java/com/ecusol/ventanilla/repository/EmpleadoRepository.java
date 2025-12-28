//ubi: src/main/java/com/ecusol/ventanilla/repository/EmpleadoRepository.java
package com.ecusol.ventanilla.repository;
import com.ecusol.ventanilla.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findByUsuario(String usuario);
}