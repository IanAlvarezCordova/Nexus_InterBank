package com.estructura.cbs.repository;

import com.estructura.cbs.model.Feriado;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeriadoRepository extends MongoRepository<Feriado, Integer> {
    List<Feriado> findByUbicacionId(Integer ubicacionId);
}
