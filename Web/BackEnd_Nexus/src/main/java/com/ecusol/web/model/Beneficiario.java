//ubi: src/main/java/com/ecusol/web/model/Beneficiario.java
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
    @Column(name = "beneficiarioid") // SQL: beneficiario_id (snake_case)
    private Integer beneficiarioId;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy para evitar logs gigantes
    @JoinColumn(name = "usuariowebid", nullable = false) // SQL: usuario_web_id
    private UsuarioWeb usuarioWeb;

    @Column(name = "numerocuentadestino", nullable = false)
    private String numeroCuentaDestino;

    @Column(name = "nombretitular", nullable = false)
    private String nombreTitular;

    @Column(name = "tipocuenta") // Agregado para integridad
    private String tipoCuenta;

    @Column(name = "alias")
    private String alias;

    @Column(name = "fecharegistro")
    private LocalDateTime fechaRegistro;
}