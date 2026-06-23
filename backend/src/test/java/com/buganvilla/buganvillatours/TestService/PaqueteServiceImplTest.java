package com.buganvilla.buganvillatours.TestService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.repository.PaqueteRepository;
import com.buganvilla.buganvillatours.service.impl.PaqueteServiceImpl;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaqueteServiceImplTest {

    private PaqueteRepository paqueteRepository;
    private PaqueteServiceImpl paqueteService;

    @BeforeEach
    void setUp() {
        paqueteRepository = mock(PaqueteRepository.class);
        paqueteService = new PaqueteServiceImpl(paqueteRepository);
    }

    private Paquete crearPaquete(Long id) {
        Paquete p = new Paquete();
        p.setIdPaquete(id);
        p.setNombrePaquete("Tour Cusco");
        p.setDescripcion("Viaje completo");
        p.setPrecioBase(BigDecimal.valueOf(500));
        p.setDuracionDias(5);
        p.setEstado("activo");
        return p;
    }
    @Test
    void testFindAll() {
        List<Paquete> lista = List.of(crearPaquete(1L));
        when(paqueteRepository.findAll()).thenReturn(lista);

        List<Paquete> resultado = paqueteService.findAll();

        assertEquals(1, resultado.size());
        verify(paqueteRepository).findAll();
    }
    @Test
    void testFindById() {
        Paquete p = crearPaquete(1L);
        when(paqueteRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Paquete> resultado = paqueteService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdPaquete());
    }

    @Test
    void testSave() {
        Paquete p = crearPaquete(1L);
        when(paqueteRepository.save(p)).thenReturn(p);

        Paquete resultado = paqueteService.save(p);

        assertNotNull(resultado);
        verify(paqueteRepository).save(p);
    }

    @Test
    void testUpdateSuccess() {
        Paquete existente = crearPaquete(1L);
        Paquete nuevo = crearPaquete(1L);
        nuevo.setNombrePaquete("Nuevo nombre");

        when(paqueteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(paqueteRepository.save(any(Paquete.class))).thenReturn(existente);

        Paquete actualizado = paqueteService.update(1L, nuevo);

        assertEquals("Nuevo nombre", actualizado.getNombrePaquete());
        verify(paqueteRepository).save(existente);
    }

    @Test
    void testUpdateNotFound() {
        when(paqueteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> paqueteService.update(1L, crearPaquete(1L))
        );

        assertEquals("Paquete no encontrado con ID: 1", ex.getMessage());
    }

    @Test
    void testDeleteById() {
        paqueteService.deleteById(1L);
        verify(paqueteRepository).deleteById(1L);
    }

    @Test
    void testFindByNombre() {
        List<Paquete> lista = List.of(crearPaquete(1L));

        when(paqueteRepository.findByNombrePaqueteContainingIgnoreCase("cusco"))
                .thenReturn(lista);

        List<Paquete> resultado = paqueteService.findByNombre("cusco");

        assertEquals(1, resultado.size());
    }

    @Test
    void testFindByEstado() {
        when(paqueteRepository.findByEstado("activo"))
                .thenReturn(List.of(crearPaquete(1L)));

        List<Paquete> r = paqueteService.findByEstado("activo");

        assertEquals(1, r.size());
    }

    @Test
    void testFindActivos() {
        when(paqueteRepository.findByEstadoOrderByNombrePaquete("activo"))
                .thenReturn(List.of(crearPaquete(1L)));

        List<Paquete> r = paqueteService.findActivos();

        assertEquals(1, r.size());
    }

    @Test
    void testFindByPrecioBetween() {
        when(paqueteRepository.findByPrecioBaseBetween(BigDecimal.ZERO, BigDecimal.TEN))
                .thenReturn(List.of(crearPaquete(1L)));

        List<Paquete> r = paqueteService.findByPrecioBetween(BigDecimal.ZERO, BigDecimal.TEN);

        assertEquals(1, r.size());
    }

    @Test
    void testFindByLugar() {
        when(paqueteRepository.findByLugarIdLugar(99L))
                .thenReturn(List.of(crearPaquete(1L)));

        List<Paquete> r = paqueteService.findByLugar(99L);

        assertEquals(1, r.size());
    }

    @Test
    void testBuscarConFiltros() {
        when(paqueteRepository.buscarPaquetesConFiltros(any(), any(), any(), any()))
                .thenReturn(List.of(crearPaquete(1L)));

        List<Paquete> r = paqueteService.buscarConFiltros("cusco", null, null, "activo");

        assertEquals(1, r.size());
    }

    @Test
    void testDesactivarPaquete() {
        Paquete p = crearPaquete(1L);
        when(paqueteRepository.findById(1L)).thenReturn(Optional.of(p));
        when(paqueteRepository.save(p)).thenReturn(p);

        Paquete r = paqueteService.desactivarPaquete(1L);

        assertEquals("inactivo", r.getEstado());
    }

    @Test
    void testActivarPaquete() {
        Paquete p = crearPaquete(1L);
        p.setEstado("inactivo");

        when(paqueteRepository.findById(1L)).thenReturn(Optional.of(p));
        when(paqueteRepository.save(p)).thenReturn(p);

        Paquete r = paqueteService.activarPaquete(1L);

        assertEquals("activo", r.getEstado());
    }
}