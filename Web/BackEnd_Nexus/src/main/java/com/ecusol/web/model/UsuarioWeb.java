//ubi: src/main/java/com/ecusol/web/model/UsuarioWeb.java
package com.ecusol.web.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarioweb", schema = "nexus_web")
@Data
public class UsuarioWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuariowebid")
    private Integer usuarioWebId;

    @Column(name = "clienteidcore", nullable = false)
    private Integer clienteIdCore;

    @Column(name = "usuario", nullable = false, unique = true)
    private String usuario;

    @Column(name = "contrasenahash", nullable = false)
    private String password;

    @Column(name = "emailcontacto", nullable = false)
    private String email;

    @Column(nullable = false)
    private String estado;

    @Column(name = "intentosfallidos")
    private Integer intentosFallidos;

    @Column(name = "ultimoacceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "fecharegistro")
    private LocalDateTime fechaRegistro;
}