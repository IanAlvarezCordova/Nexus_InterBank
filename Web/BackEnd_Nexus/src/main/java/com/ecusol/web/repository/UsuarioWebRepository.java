//ubi: src/main/java/com/ecusol/web/repository/UsuarioWebRepository.java
package com.ecusol.web.repository;
import com.ecusol.web.model.UsuarioWeb;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioWebRepository extends JpaRepository<UsuarioWeb, Integer> {
    Optional<UsuarioWeb> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}