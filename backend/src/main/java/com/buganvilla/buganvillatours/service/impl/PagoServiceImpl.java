package com.buganvilla.buganvillatours.service.impl;

import com.buganvilla.buganvillatours.model.entity.Pago;
import com.buganvilla.buganvillatours.repository.PagoRepository;
import com.buganvilla.buganvillatours.service.PagoService;
import com.buganvilla.buganvillatours.service.ReservaService;
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
    private final ReservaService reservaService;

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findAll() {
        log.info("Buscando todos los pagos");
        return pagoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> findById(Long id) {
        log.info("Buscando pago por ID: {}", id);
        return pagoRepository.findById(id);
    }

    @Override
    @Transactional
    public Pago save(Pago pago) {
        log.info("Creando nuevo pago para reserva ID: {}", pago.getReserva().getIdReserva());
        return pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public Pago update(Long id, Pago pago) {
        log.info("Actualizando pago ID: {}", id);

        return pagoRepository.findById(id)
                .map(pagoExistente -> {
                    pagoExistente.setMonto(pago.getMonto());
                    pagoExistente.setMetodo(pago.getMetodo());
                    pagoExistente.setEstado(pago.getEstado());
                    return pagoRepository.save(pagoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando pago ID: {}", id);
        pagoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findByReserva(Long idReserva) {
        return pagoRepository.findByReservaIdReserva(idReserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findByEstado(String estado) {
        return pagoRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findByMetodo(String metodo) {
        return pagoRepository.findByMetodo(metodo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pagoRepository.findByFechaPagoBetween(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pago> findUltimoPagoByReserva(Long idReserva) {
        return pagoRepository.findFirstByReservaIdReservaOrderByFechaPagoDesc(idReserva);
    }

    @Override
    @Transactional
    public Pago procesarPago(Long id) {
        log.info("Procesando pago ID: {}", id);

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
        log.info("Rechazando pago ID: {}", id);

        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.rechazarPago();
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> findPagosPendientesByUsuario(Long idUsuario) {
        return pagoRepository.findPagosPendientesByUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPagosCompletadosPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pagoRepository.findTotalPagosCompletadosPorPeriodo(fechaInicio, fechaFin)
                .orElse(BigDecimal.ZERO);
    }
}