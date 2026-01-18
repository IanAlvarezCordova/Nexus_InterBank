package com.nexus.ms_transacciones.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transaccion")
@Getter
@Setter
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaccion_id")
    private Integer transaccionId;

    private String cuentaOrigen;

    @Column(name = "cuenta_destino")
    private String cuentaDestino;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "descripcion")
    private String descripcion;
    private String estado; 
    private String rolTransaccion; 
    private LocalDateTime fechaEjecucion;

    @Column(unique = true)
    private String instructionId;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "id_banco_origen")
    private Integer idBancoOrigen;

    @Column(name = "id_banco_destino")
    private Integer idBancoDestino;

    @Column(name = "mensaje_error")
    private String mensajeError;

    @Version
    @Column(name = "version")
    private Long version;

    // 7. Constructor vacío sin Lombok
    public Transaccion() {
    }

    // 8. Constructor solo para la clave primaria sin Lombok
    public Transaccion(Integer transaccionId) {
        this.transaccionId = transaccionId;
    }

    // 11. equals y hashCode solo comparando la clave primaria
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaccion that = (Transaccion) o;
        return Objects.equals(transaccionId, that.transaccionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaccionId);
    }

    // 12. String incluyendo todas las propiedades
    @Override
    public String toString() {
        return "Transaccion{" +
                "transaccionId=" + transaccionId +
                ", cuentaOrigen='" + cuentaOrigen + '\'' +
                ", cuentaDestino='" + cuentaDestino + '\'' +
                ", monto=" + monto +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", rolTransaccion='" + rolTransaccion + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fechaEjecucion=" + fechaEjecucion +
                ", instructionId='" + instructionId + '\'' +
                ", referencia='" + referencia + '\'' +
                ", idBancoOrigen=" + idBancoOrigen +
                ", idBancoDestino=" + idBancoDestino +
                ", mensajeError='" + mensajeError + '\'' +
                ", version=" + version +
                '}';
    }
}