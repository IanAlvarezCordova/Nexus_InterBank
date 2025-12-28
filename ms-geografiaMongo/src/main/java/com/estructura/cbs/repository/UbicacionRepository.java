package com.estructura.cbs.repository;

import com.estructura.cbs.model.UbicacionGeografica;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UbicacionRepository extends MongoRepository<UbicacionGeografica, Integer> {
    List<UbicacionGeografica> findByUbicacionPadreId(Integer ubicacionPadreId);
}
