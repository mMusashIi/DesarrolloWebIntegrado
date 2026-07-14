package com.buganvilla.buganvillatours.repository;

import com.buganvilla.buganvillatours.model.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Buscar reservas por usuario
    List<Reserva> findByUsuarioIdUsuario(Long idUsuario);

    // Buscar reservas por estado
    List<Reserva> findByEstado(String estado);

    // Buscar reservas por usuario y estado
    List<Reserva> findByUsuarioIdUsuarioAndEstado(Long idUsuario, String estado);

    // Buscar reservas por inventario
    List<Reserva> findByInventarioIdInventario(Long idInventario);

    // Buscar reservas por rango de fechas
    List<Reserva> findByFechaReservaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Contar reservas por usuario
    long countByUsuarioIdUsuario(Long idUsuario);

    // Contar reservas por estado
    long countByEstado(String estado);

    // Buscar reservas pendientes por usuario
    @Query("SELECT r FROM Reserva r WHERE r.usuario.idUsuario = :idUsuario AND r.estado = 'pendiente'")
    List<Reserva> findReservasPendientesByUsuario(@Param("idUsuario") Long idUsuario);

    // Buscar reservas confirmadas recientes
    @Query("SELECT r FROM Reserva r WHERE r.estado = 'confirmada' AND r.fechaReserva >= :fecha ORDER BY r.fechaReserva DESC")
    List<Reserva> findReservasConfirmadasRecientes(@Param("fecha") LocalDateTime fecha);

    // Estadísticas de reservas por mes
    @Query("SELECT MONTH(r.fechaReserva) as mes, COUNT(r) as total FROM Reserva r WHERE YEAR(r.fechaReserva) = :year GROUP BY MONTH(r.fechaReserva)")
    List<Object[]> findEstadisticasReservasPorMes(@Param("year") int year);
}