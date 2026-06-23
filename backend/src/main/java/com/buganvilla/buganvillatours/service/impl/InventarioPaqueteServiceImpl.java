package com.buganvilla.buganvillatours.service.impl;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.repository.InventarioPaqueteRepository;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioPaqueteServiceImpl implements InventarioPaqueteService {

    private final InventarioPaqueteRepository inventarioPaqueteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findAll() {
        log.info("Buscando todo el inventario");
        return inventarioPaqueteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventarioPaquete> findById(Long id) {
        log.info("Buscando inventario por ID: {}", id);
        return inventarioPaqueteRepository.findById(id);
    }

    @Override
    @Transactional
    public InventarioPaquete save(InventarioPaquete inventarioPaquete) {
        log.info("Guardando inventario para paquete ID: {}", inventarioPaquete.getPaquete().getIdPaquete());

        // Validar que no exista ya un inventario para la misma fecha
        Optional<InventarioPaquete> existente = inventarioPaqueteRepository
                .findByPaqueteIdPaqueteAndFechaSalida(
                        inventarioPaquete.getPaquete().getIdPaquete(),
                        inventarioPaquete.getFechaSalida()
                );

        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe inventario para este paquete en la fecha: " + inventarioPaquete.getFechaSalida());
        }

        return inventarioPaqueteRepository.save(inventarioPaquete);
    }

    @Override
    @Transactional
    public InventarioPaquete update(Long id, InventarioPaquete inventarioPaquete) {
        log.info("Actualizando inventario ID: {}", id);

        return inventarioPaqueteRepository.findById(id)
                .map(inventarioExistente -> {
                    inventarioExistente.setFechaSalida(inventarioPaquete.getFechaSalida());
                    inventarioExistente.setFechaRetorno(inventarioPaquete.getFechaRetorno());
                    inventarioExistente.setCupoTotal(inventarioPaquete.getCupoTotal());
                    inventarioExistente.setCupoDisponible(inventarioPaquete.getCupoDisponible());
                    return inventarioPaqueteRepository.save(inventarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando inventario ID: {}", id);
        inventarioPaqueteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findByPaquete(Long idPaquete) {
        return inventarioPaqueteRepository.findByPaqueteIdPaquete(idPaquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findByFechaSalida(LocalDate fechaSalida) {
        return inventarioPaqueteRepository.findByFechaSalida(fechaSalida);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return inventarioPaqueteRepository.findByFechaSalidaBetween(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findConCupoDisponible() {
        return inventarioPaqueteRepository.findByCupoDisponibleGreaterThan(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InventarioPaquete> findByPaqueteAndFecha(Long idPaquete, LocalDate fechaSalida) {
        return inventarioPaqueteRepository.findByPaqueteIdPaqueteAndFechaSalida(idPaquete, fechaSalida);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findInventarioDisponibleByPaquete(Long idPaquete) {
        return inventarioPaqueteRepository.findInventarioDisponibleByPaquete(idPaquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioPaquete> findProximasSalidasDisponibles() {
        return inventarioPaqueteRepository.findProximasSalidasDisponibles();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long idInventario, Integer cantidad) {
        Integer cupoDisponible = inventarioPaqueteRepository.findCupoDisponibleById(idInventario);
        return cupoDisponible != null && cupoDisponible >= cantidad;
    }

    @Override
    @Transactional
    public void reducirCupo(Long idInventario, Integer cantidad) {
        log.info("Reduciendo cupo en {} para inventario ID: {}", cantidad, idInventario);

        InventarioPaquete inventario = inventarioPaqueteRepository.findById(idInventario)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + idInventario));

        if (!inventario.tieneCupoDisponible(cantidad)) {
            throw new RuntimeException("No hay cupo disponible suficiente. Disponible: " + inventario.getCupoDisponible());
        }

        inventario.reducirCupo(cantidad);
        inventarioPaqueteRepository.save(inventario);
    }

    @Override
    @Transactional
    public void aumentarCupo(Long idInventario, Integer cantidad) {
        log.info("Aumentando cupo en {} para inventario ID: {}", cantidad, idInventario);

        InventarioPaquete inventario = inventarioPaqueteRepository.findById(idInventario)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con ID: " + idInventario));

        inventario.aumentarCupo(cantidad);
        inventarioPaqueteRepository.save(inventario);
    }
}

