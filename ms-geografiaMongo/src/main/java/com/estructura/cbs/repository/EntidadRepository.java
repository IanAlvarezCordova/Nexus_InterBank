package com.estructura.cbs.repository;

import com.estructura.cbs.model.Entidad;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntidadRepository extends MongoRepository<Entidad, Integer> {
    boolean existsByRuc(String ruc);
}
