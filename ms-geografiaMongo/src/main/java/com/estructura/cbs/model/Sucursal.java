package com.estructura.cbs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Objects;

@Document(collection = "sucursal")
@Getter
@Setter
public class Sucursal {

    @Id
    @Field("sucursalId")
    private Integer sucursalId;

    @Field("entidadId")
    private Integer entidadId;

    @Field("ubicacionId")
    private Integer ubicacionId;

    @Field("codigoSucursal")
    private String codigoSucursal;

    @Field("nombre")
    private String nombre;

    @Field("direccion")
    private String direccion;

    @Field("telefono")
    private String telefono;

    @Field("latitud")
    private BigDecimal latitud;

    @Field("longitud")
    private BigDecimal longitud;

    @Field("estado")
    private String estado;

    public Sucursal() {}

    public Sucursal(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sucursal)) return false;
        Sucursal other = (Sucursal) o;
        return sucursalId != null && sucursalId.equals(other.sucursalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sucursalId);
    }

    @Override
    public String toString() {
        return "Sucursal{" +
                "sucursalId=" + sucursalId +
                ", entidadId=" + entidadId +
                ", ubicacionId=" + ubicacionId +
                ", codigoSucursal='" + codigoSucursal + '\'' +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", estado='" + estado + '\'' +
                '}';
    }
}
