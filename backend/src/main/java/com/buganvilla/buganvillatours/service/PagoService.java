package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.entity.Pago;

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

    // Métodos específicos
    List<Pago> findByReserva(Long idReserva);
    List<Pago> findByEstado(String estado);
    List<Pago> findByMetodo(String metodo);
    List<Pago> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Optional<Pago> findUltimoPagoByReserva(Long idReserva);
    Pago procesarPago(Long id);
    Pago rechazarPago(Long id);
    List<Pago> findPagosPendientesByUsuario(Long idUsuario);
    BigDecimal getTotalPagosCompletadosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

