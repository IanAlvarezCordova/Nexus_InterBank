package com.estructura.cbs.repository;

import com.estructura.cbs.model.Sucursal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SucursalRepository extends MongoRepository<Sucursal, Integer> {
    List<Sucursal> findByEntidadId(Integer entidadId);
    List<Sucursal> findByUbicacionId(Integer ubicacionId);
}
