package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventarioPaqueteService {

    List<InventarioPaquete> findAll();
    Optional<InventarioPaquete> findById(Long id);
    InventarioPaquete save(InventarioPaquete inventarioPaquete);
    InventarioPaquete update(Long id, InventarioPaquete inventarioPaquete);
    void deleteById(Long id);

    // Métodos específicos
    List<InventarioPaquete> findByPaquete(Long idPaquete);
    List<InventarioPaquete> findByFechaSalida(LocalDate fechaSalida);
    List<InventarioPaquete> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin);
    List<InventarioPaquete> findConCupoDisponible();
    Optional<InventarioPaquete> findByPaqueteAndFecha(Long idPaquete, LocalDate fechaSalida);
    List<InventarioPaquete> findInventarioDisponibleByPaquete(Long idPaquete);
    List<InventarioPaquete> findProximasSalidasDisponibles();
    boolean verificarDisponibilidad(Long idInventario, Integer cantidad);
    void reducirCupo(Long idInventario, Integer cantidad);
    void aumentarCupo(Long idInventario, Integer cantidad);
}


