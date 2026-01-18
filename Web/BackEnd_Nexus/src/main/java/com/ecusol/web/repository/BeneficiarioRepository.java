package com.ecusol.web.repository;

import com.ecusol.web.model.Beneficiario; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Integer> {
    List<Beneficiario> findByUsuarioWeb_UsuarioWebId(Integer usuarioWebId);
}