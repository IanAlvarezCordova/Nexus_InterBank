package com.nexus.ms_clientes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "persona")
@PrimaryKeyJoinColumn(name = "clienteid")
@Getter
@Setter
public class Persona extends Cliente {

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "numeroidentificacion", unique = true, nullable = false, length = 13)
    private String numeroIdentificacion;

    @Column(name = "tipoidentificacion", nullable = false, length = 15)
    private String tipoIdentificacion;

    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "fechanacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    public Persona() {
        super();
    }

    public Persona(Integer clienteId) {
        super(clienteId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Persona persona = (Persona) o;

        return getClienteId() != null && getClienteId().equals(persona.getClienteId());
    }

    @Override
    public int hashCode() {
        return getClienteId() != null ? getClienteId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "clienteId=" + getClienteId() +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", numeroIdentificacion='" + numeroIdentificacion + '\'' +
                ", tipoIdentificacion='" + tipoIdentificacion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", tipoCliente='" + getTipoCliente() + '\'' +
                ", estado='" + getEstado() + '\'' +
                ", fechaRegistro=" + getFechaRegistro() +
                '}';
    }
}
