package com.buganvilla.buganvillatours.repository;

import com.buganvilla.buganvillatours.model.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pagos por reserva
    List<Pago> findByReservaIdReserva(Long idReserva);

    // Buscar pagos por estado
    List<Pago> findByEstado(String estado);

    // Buscar pagos por metodo
    List<Pago> findByMetodo(String metodo);

    // Buscar pagos por rango de fechas
    List<Pago> findByFechaPagoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar pagos por rango de montos
    List<Pago> findByMontoBetween(BigDecimal montoMin, BigDecimal montoMax);

    // Buscar último pago por reserva
    Optional<Pago> findFirstByReservaIdReservaOrderByFechaPagoDesc(Long idReserva);

    // Sumar total de pagos completados
    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estado = 'completado' AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    Optional<BigDecimal> findTotalPagosCompletadosPorPeriodo(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    // Contar pagos por estado
    @Query("SELECT p.estado, COUNT(p) FROM Pago p GROUP BY p.estado")
    List<Object[]> countPagosByEstado();

    // Buscar pagos pendientes por usuario
    @Query("SELECT p FROM Pago p WHERE p.reserva.usuario.idUsuario = :idUsuario AND p.estado = 'pendiente'")
    List<Pago> findPagosPendientesByUsuario(@Param("idUsuario") Long idUsuario);

    // Estadísticas de pagos por metodo
    @Query("SELECT p.metodo, COUNT(p), SUM(p.monto) FROM Pago p WHERE p.estado = 'completado' GROUP BY p.metodo")
    List<Object[]> findEstadisticasPagosPorMetodo();
}
