package com.estructura.cbs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Document(collection = "entidad")
@Getter
@Setter
public class Entidad {

    @Id
    @Field("entidadId")
    private Integer entidadId;

    @Field("ruc")
    private String ruc;

    @Field("razonSocial")
    private String razonSocial;

    @Field("nombreComercial")
    private String nombreComercial;

    @Field("estado")
    private String estado;

    public Entidad() {}

    public Entidad(Integer entidadId) {
        this.entidadId = entidadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entidad)) return false;
        Entidad other = (Entidad) o;
        return entidadId != null && entidadId.equals(other.entidadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entidadId);
    }

    @Override
    public String toString() {
        return "Entidad{" +
                "entidadId=" + entidadId +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", nombreComercial='" + nombreComercial + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
