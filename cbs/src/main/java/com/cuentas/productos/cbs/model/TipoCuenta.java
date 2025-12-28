package com.cuentas.productos.cbs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tipocuenta")          
@Getter
@Setter                              
public class TipoCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipocuentaid")   
    private Integer id;              

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Column(name = "tipoamortizacion", nullable = false, length = 20)
    private String tipoAmortizacion;

    public TipoCuenta() {
    }

   
    public TipoCuenta(Integer id) {
        this.id = id;
    }

  
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TipoCuenta that = (TipoCuenta) o;
        return id != null && id.equals(that.id);
    }

   
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

   
    @Override
    public String toString() {
        return "TipoCuenta{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado='" + estado + '\'' +
                ", tipoAmortizacion='" + tipoAmortizacion + '\'' +
                '}';
    }
}
