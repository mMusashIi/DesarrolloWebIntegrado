package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.entity.Paquete;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaqueteService {

    List<Paquete> findAll();
    Optional<Paquete> findById(Long id);
    Paquete save(Paquete paquete);
    Paquete update(Long id, Paquete paquete);
    void deleteById(Long id);

    // Métodos específicos
    List<Paquete> findByNombre(String nombrePaquete);
    List<Paquete> findByEstado(String estado);
    List<Paquete> findActivos();
    List<Paquete> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
    List<Paquete> findByLugar(Long idLugar);
    List<Paquete> buscarConFiltros(String nombre, BigDecimal precioMin, BigDecimal precioMax, String estado);
    Paquete desactivarPaquete(Long id);
    Paquete activarPaquete(Long id);
}

