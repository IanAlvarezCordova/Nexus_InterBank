package com.nexus.ms_clientes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clienteid")
    private Integer clienteId;

    @Column(name = "tipocliente", nullable = false, length = 1)
    private String tipoCliente;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Column(name = "fecharegistro", nullable = false)
    private LocalDate fechaRegistro;

    public Cliente() {
    }

    public Cliente(Integer clienteId) {
        this.clienteId = clienteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        return clienteId != null && clienteId.equals(cliente.clienteId);
    }

    @Override
    public int hashCode() {
        return clienteId != null ? clienteId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "clienteId=" + clienteId +
                ", tipoCliente='" + tipoCliente + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
