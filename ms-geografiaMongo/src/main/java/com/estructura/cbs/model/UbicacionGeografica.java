package com.estructura.cbs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Document(collection = "ubicacionGeografica")
@Getter
@Setter
public class UbicacionGeografica {

    @Id
    @Field("ubicacionId")
    private Integer ubicacionId;

    @Field("nombre")
    private String nombre;

    @Field("tipoUbicacion")
    private String tipoUbicacion;

    @Field("ubicacionPadreId")
    private Integer ubicacionPadreId;

    public UbicacionGeografica() {}

    public UbicacionGeografica(Integer ubicacionId) {
        this.ubicacionId = ubicacionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UbicacionGeografica)) return false;
        UbicacionGeografica other = (UbicacionGeografica) o;
        return ubicacionId != null && ubicacionId.equals(other.ubicacionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ubicacionId);
    }

    @Override
    public String toString() {
        return "UbicacionGeografica{" +
                "ubicacionId=" + ubicacionId +
                ", nombre='" + nombre + '\'' +
                ", tipoUbicacion='" + tipoUbicacion + '\'' +
                ", ubicacionPadreId=" + ubicacionPadreId +
                '}';
    }
}
