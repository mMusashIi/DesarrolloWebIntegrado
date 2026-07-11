package com.backend.pago_service.service.impl;

import com.backend.pago_service.model.entity.Pago;
import com.backend.pago_service.repository.PagoRepository;
import com.backend.pago_service.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findAll() {
        return pagoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> findById(Long id) {
        return pagoRepository.findById(id);
    }

    @Override
    @Transactional
    public Pago save(Pago pago) {
        log.info("Creando pago para reserva ID: {}", pago.getIdReserva());
        return pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public Pago update(Long id, Pago pago) {
        return pagoRepository.findById(id)
                .map(existing -> {
                    existing.setMonto(pago.getMonto());
                    existing.setMetodo(pago.getMetodo());
                    existing.setEstado(pago.getEstado());
                    return pagoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        pagoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findByReserva(Long idReserva) {
        return pagoRepository.findByIdReserva(idReserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findByEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }

    @Override
    @Transactional
    public Pago procesarPago(Long id) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.procesarPago();
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Pago rechazarPago(Long id) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.rechazarPago();
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPagosCompletadosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pagoRepository.findTotalPagosCompletadosPorPeriodo(fechaInicio, fechaFin)
                .orElse(BigDecimal.ZERO);
    }
}
