package com.nexus.ms_clientes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@PrimaryKeyJoinColumn(name = "clienteid")
@Getter
@Setter
public class Empresa extends Cliente {

    @Column(name = "ruc", unique = true, nullable = false, length = 13)
    private String ruc;

    @Column(name = "razonsocial", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "correoelectronico", nullable = false, length = 100)
    private String correoElectronico;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    public Empresa() {
        super();
    }

    public Empresa(Integer clienteId) {
        super(clienteId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Empresa empresa = (Empresa) o;

        return getClienteId() != null && getClienteId().equals(empresa.getClienteId());
    }

    @Override
    public int hashCode() {
        return getClienteId() != null ? getClienteId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "clienteId=" + getClienteId() +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", telefono='" + telefono + '\'' +
                ", tipoCliente='" + getTipoCliente() + '\'' +
                ", estado='" + getEstado() + '\'' +
                ", fechaRegistro=" + getFechaRegistro() +
                '}';
    }
}
