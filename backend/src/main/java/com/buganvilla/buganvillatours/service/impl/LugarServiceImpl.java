package com.buganvilla.buganvillatours.service.impl;

import com.buganvilla.buganvillatours.model.entity.Lugar;
import com.buganvilla.buganvillatours.repository.LugarRepository;
import com.buganvilla.buganvillatours.service.LugarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LugarServiceImpl implements LugarService {

    private final LugarRepository lugarRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Lugar> findAll() {
        log.info("Buscando todos los lugares");
        return lugarRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Lugar> findById(Long id) {
        log.info("Buscando lugar por ID: {}", id);
        return lugarRepository.findById(id);
    }

    @Override
    @Transactional
    public Lugar save(Lugar lugar) {
        log.info("Guardando lugar: {}", lugar.getNombreLugar());

        if (lugarRepository.existsByNombreLugar(lugar.getNombreLugar())) {
            throw new RuntimeException("Ya existe un lugar con el nombre: " + lugar.getNombreLugar());
        }

        return lugarRepository.save(lugar);
    }

    @Override
    @Transactional
    public Lugar update(Long id, Lugar lugar) {
        log.info("Actualizando lugar ID: {}", id);

        return lugarRepository.findById(id)
                .map(lugarExistente -> {
                    lugarExistente.setNombreLugar(lugar.getNombreLugar());
                    lugarExistente.setCiudad(lugar.getCiudad());
                    lugarExistente.setDescripcion(lugar.getDescripcion());
                    return lugarRepository.save(lugarExistente);
                })
                .orElseThrow(() -> new RuntimeException("Lugar no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando lugar ID: {}", id);
        lugarRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Lugar> findByNombreLugar(String nombreLugar) {
        return lugarRepository.findByNombreLugar(nombreLugar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lugar> findByCiudad(String ciudad) {
        return lugarRepository.findByCiudadContainingIgnoreCase(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lugar> findByNombreLugarContaining(String nombreLugar) {
        return lugarRepository.findByNombreLugarContainingIgnoreCase(nombreLugar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lugar> buscarPorTermino(String termino) {
        return lugarRepository.findByNombreLugarOrCiudadContainingIgnoreCase(termino);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombreLugar(String nombreLugar) {
        return lugarRepository.existsByNombreLugar(nombreLugar);
    }
}

