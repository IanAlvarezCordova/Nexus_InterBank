package com.ecusol.web.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiario", schema = "nexus_web")
@Data
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beneficiarioid") 
    private Integer beneficiarioId;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "usuariowebid", nullable = false) 
    private UsuarioWeb usuarioWeb;

    @Column(name = "numerocuentadestino", nullable = false)
    private String numeroCuentaDestino;

    @Column(name = "nombretitular", nullable = false)
    private String nombreTitular;

    @Column(name = "tipocuenta")
    private String tipoCuenta;

    @Column(name = "alias")
    private String alias;

    @Column(name = "fecharegistro")
    private LocalDateTime fechaRegistro;
}