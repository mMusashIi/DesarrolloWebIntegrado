package com.backend.reserva_service.repository;

import com.backend.reserva_service.model.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByIdUsuario(Long idUsuario);

    List<Reserva> findByEstado(String estado);

    List<Reserva> findByIdUsuarioAndEstado(Long idUsuario, String estado);

    List<Reserva> findByIdInventario(Long idInventario);

    List<Reserva> findByFechaReservaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    long countByIdUsuario(Long idUsuario);

    @Query("SELECT r FROM Reserva r WHERE r.idUsuario = :idUsuario AND r.estado = 'pendiente'")
    List<Reserva> findReservasPendientesByUsuario(@Param("idUsuario") Long idUsuario);
}
