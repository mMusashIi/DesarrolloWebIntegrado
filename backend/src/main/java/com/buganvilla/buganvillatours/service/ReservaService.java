package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.entity.Reserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaService {

    List<Reserva> findAll();
    Optional<Reserva> findById(Long id);
    Reserva save(Reserva reserva);
    Reserva update(Long id, Reserva reserva);
    void deleteById(Long id);

    // Métodos específicos
    List<Reserva> findByUsuario(Long idUsuario);
    List<Reserva> findByEstado(String estado);
    List<Reserva> findByUsuarioAndEstado(Long idUsuario, String estado);
    List<Reserva> findByInventario(Long idInventario);
    List<Reserva> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Reserva cancelarReserva(Long id);
    Reserva confirmarReserva(Long id);
    List<Reserva> findReservasPendientesByUsuario(Long idUsuario);
    long countByUsuario(Long idUsuario);
}

