package com.backend.catalogo_service.service;

import com.backend.catalogo_service.model.entity.Lugar;

import java.util.List;
import java.util.Optional;

public interface LugarService {

    List<Lugar> findAll();
    Optional<Lugar> findById(Long id);
    Lugar save(Lugar lugar);
    Lugar update(Long id, Lugar lugar);
    void deleteById(Long id);

    // Métodos específicos
    Optional<Lugar> findByNombreLugar(String nombreLugar);
    List<Lugar> findByCiudad(String ciudad);
    List<Lugar> findByNombreLugarContaining(String nombreLugar);
    List<Lugar> buscarPorTermino(String termino);
    boolean existsByNombreLugar(String nombreLugar);
}
