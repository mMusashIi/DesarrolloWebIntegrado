package com.backend.pago_service.repository;

import com.backend.pago_service.model.entity.Pago;
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

    List<Pago> findByIdReserva(Long idReserva);

    List<Pago> findByEstado(String estado);

    List<Pago> findByMetodo(String metodo);

    List<Pago> findByFechaPagoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Pago> findFirstByIdReservaOrderByFechaPagoDesc(Long idReserva);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estado = 'completado' AND p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    Optional<BigDecimal> findTotalPagosCompletadosPorPeriodo(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
