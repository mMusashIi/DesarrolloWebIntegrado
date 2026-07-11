package com.backend.pago_service.service;

import com.backend.pago_service.model.entity.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoService {
    List<Pago> findAll();
    Optional<Pago> findById(Long id);
    Pago save(Pago pago);
    Pago update(Long id, Pago pago);
    void deleteById(Long id);
    List<Pago> findByReserva(Long idReserva);
    List<Pago> findByEstado(String estado);
    Pago procesarPago(Long id);
    Pago rechazarPago(Long id);
    BigDecimal getTotalPagosCompletadosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
