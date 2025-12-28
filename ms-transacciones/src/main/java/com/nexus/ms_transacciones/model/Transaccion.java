package com.nexus.ms_transacciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion")
@Data
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transaccionId;

    // Negocio
    private String cuentaOrigen;
    private String cuentaDestino;
    private BigDecimal monto;
    private String descripcion;
    private String estado; // PENDING, COMPLETED, FAILED
    private String rolTransaccion; // DEBITO, CREDITO
    // Tipo funcional: DEPOSITO, RETIRO, TRANSFERENCIA
    private String tipo;
    private LocalDateTime fechaEjecucion;

    // Switch (Técnico)
    @Column(unique = true)
    private String instructionId;
    private String referencia;
    private Integer idBancoOrigen;
    private Integer idBancoDestino;
    private String mensajeError;

    // Concurrencia
    @Version
    private Long version;
}