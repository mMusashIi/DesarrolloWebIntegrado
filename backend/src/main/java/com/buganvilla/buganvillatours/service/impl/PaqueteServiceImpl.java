package com.buganvilla.buganvillatours.service.impl;

import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.repository.PaqueteRepository;
import com.buganvilla.buganvillatours.service.PaqueteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaqueteServiceImpl implements PaqueteService {

    private final PaqueteRepository paqueteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findAll() {
        log.info("Buscando todos los paquetes");
        return paqueteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paquete> findById(Long id) {
        log.info("Buscando paquete por ID: {}", id);
        return paqueteRepository.findById(id);
    }

    @Override
    @Transactional
    public Paquete save(Paquete paquete) {
        log.info("Guardando paquete: {}", paquete.getNombrePaquete());
        return paqueteRepository.save(paquete);
    }

    @Override
    @Transactional
    public Paquete update(Long id, Paquete paquete) {
        log.info("Actualizando paquete ID: {}", id);

        return paqueteRepository.findById(id)
                .map(paqueteExistente -> {
                    paqueteExistente.setNombrePaquete(paquete.getNombrePaquete());
                    paqueteExistente.setDescripcion(paquete.getDescripcion());
                    paqueteExistente.setPrecioBase(paquete.getPrecioBase());
                    paqueteExistente.setDuracionDias(paquete.getDuracionDias());
                    paqueteExistente.setEstado(paquete.getEstado());
                    paqueteExistente.setLugar(paquete.getLugar());
                    return paqueteRepository.save(paqueteExistente);
                })
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando paquete ID: {}", id);
        paqueteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findByNombre(String nombrePaquete) {
        return paqueteRepository.findByNombrePaqueteContainingIgnoreCase(nombrePaquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findByEstado(String estado) {
        return paqueteRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findActivos() {
        return paqueteRepository.findByEstadoOrderByNombrePaquete("activo");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax) {
        return paqueteRepository.findByPrecioBaseBetween(precioMin, precioMax);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findByLugar(Long idLugar) {
        return paqueteRepository.findByLugarIdLugar(idLugar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> buscarConFiltros(String nombre, BigDecimal precioMin, BigDecimal precioMax, String estado) {
        return paqueteRepository.buscarPaquetesConFiltros(nombre, precioMin, precioMax, estado);
    }

    @Override
    @Transactional
    public Paquete desactivarPaquete(Long id) {
        log.info("Desactivando paquete ID: {}", id);

        return paqueteRepository.findById(id)
                .map(paquete -> {
                    paquete.setEstado("inactivo");
                    return paqueteRepository.save(paquete);
                })
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Paquete activarPaquete(Long id) {
        log.info("Activando paquete ID: {}", id);

        return paqueteRepository.findById(id)
                .map(paquete -> {
                    paquete.setEstado("activo");
                    return paqueteRepository.save(paquete);
                })
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con ID: " + id));
    }
}


