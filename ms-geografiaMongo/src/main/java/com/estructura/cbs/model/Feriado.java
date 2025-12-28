package com.estructura.cbs.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Objects;

@Document(collection = "feriado")
@Getter
@Setter
public class Feriado {

    @Id
    @Field("feriadoId")
    private Integer feriadoId;

    @Field("ubicacionId")
    private Integer ubicacionId;

    @Field("fecha")
    private LocalDate fecha;

    @Field("descripcion")
    private String descripcion;

    public Feriado() {}

    public Feriado(Integer feriadoId) {
        this.feriadoId = feriadoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feriado)) return false;
        Feriado other = (Feriado) o;
        return feriadoId != null && feriadoId.equals(other.feriadoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feriadoId);
    }

    @Override
    public String toString() {
        return "Feriado{" +
                "feriadoId=" + feriadoId +
                ", ubicacionId=" + ubicacionId +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
