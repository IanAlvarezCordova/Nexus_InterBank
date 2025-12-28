package com.cuentas.productos.cbs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tasaintereshistorico")
@Getter
@Setter
public class TasaInteresHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tasaintereshistoricoid")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipocuentaid", nullable = false)
    private TipoCuenta tipoCuenta;

    @Column(name = "tasamensual", nullable = false)
    private Double tasaMensual;

    @Column(name = "fechainiciovigencia", nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(name = "fechafinvigencia")
    private LocalDate fechaFinVigencia;

    public TasaInteresHistorico() {
    }

    public TasaInteresHistorico(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TasaInteresHistorico that = (TasaInteresHistorico) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TasaInteresHistorico{" +
                "id=" + id +
                ", tipoCuenta=" + (tipoCuenta != null ? tipoCuenta.getId() : null) +
                ", tasaMensual=" + tasaMensual +
                ", fechaInicioVigencia=" + fechaInicioVigencia +
                ", fechaFinVigencia=" + fechaFinVigencia +
                '}';
    }
}

