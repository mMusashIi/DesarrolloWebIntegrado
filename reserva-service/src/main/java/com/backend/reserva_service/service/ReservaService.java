package com.backend.reserva_service.service;

import com.backend.reserva_service.model.entity.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    List<Reserva> findAll();
    Optional<Reserva> findById(Long id);
    Reserva save(Reserva reserva);
    Reserva update(Long id, Reserva reserva);
    void deleteById(Long id);
    List<Reserva> findByUsuario(Long idUsuario);
    List<Reserva> findByEstado(String estado);
    List<Reserva> findByInventario(Long idInventario);
    Reserva cancelarReserva(Long id);
    Reserva confirmarReserva(Long id);
    long countByUsuario(Long idUsuario);
}
