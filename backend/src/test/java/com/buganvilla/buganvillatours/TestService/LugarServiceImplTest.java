package com.buganvilla.buganvillatours.TestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;


import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.buganvilla.buganvillatours.model.entity.Lugar;
import com.buganvilla.buganvillatours.repository.LugarRepository;
import com.buganvilla.buganvillatours.service.impl.LugarServiceImpl;

class LugarServiceImplTest {

    @Mock
    private LugarRepository lugarRepository;

    @InjectMocks
    private LugarServiceImpl lugarService;

    private Lugar lugar;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        lugar = Lugar.builder()
                .idLugar(1L)
                .nombreLugar("Machu Picchu")
                .ciudad("Cusco")
                .descripcion("Ciudadela Inca")
                .build();
    }

    @Test
    void testFindAll() {
        when(lugarRepository.findAll()).thenReturn(List.of(lugar));

        List<Lugar> result = lugarService.findAll();

        assertEquals(1, result.size());
        verify(lugarRepository).findAll();
    }

    @Test
    void testFindById() {
        when(lugarRepository.findById(1L)).thenReturn(Optional.of(lugar));

        Optional<Lugar> result = lugarService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Machu Picchu", result.get().getNombreLugar());
        verify(lugarRepository).findById(1L);
    }
    
    @Test
    void testSave() {
        when(lugarRepository.existsByNombreLugar("Machu Picchu")).thenReturn(false);
        when(lugarRepository.save(lugar)).thenReturn(lugar);

        Lugar result = lugarService.save(lugar);

        assertNotNull(result);
        assertEquals("Machu Picchu", result.getNombreLugar());
        verify(lugarRepository).save(lugar);
    }
    
    @Test
    void testSaveLugarExistente() {
        when(lugarRepository.existsByNombreLugar("Machu Picchu")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> lugarService.save(lugar));

        assertTrue(ex.getMessage().contains("Ya existe un lugar"));
        verify(lugarRepository, never()).save(any());
    }

    // ------------------------------
    // TEST update()
    // ------------------------------
    @Test
    void testUpdate() {
        Lugar nuevo = Lugar.builder()
                .nombreLugar("Nuevo Lugar")
                .ciudad("Nueva Ciudad")
                .descripcion("Otra desc")
                .build();

        when(lugarRepository.findById(1L)).thenReturn(Optional.of(lugar));
        when(lugarRepository.save(any(Lugar.class))).thenAnswer(i -> i.getArgument(0));

        Lugar result = lugarService.update(1L, nuevo);

        assertEquals("Nuevo Lugar", result.getNombreLugar());
        assertEquals("Nueva Ciudad", result.getCiudad());
        verify(lugarRepository).save(any(Lugar.class));
    }

    @Test
    void testUpdateNotFound() {
        when(lugarRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lugarService.update(1L, lugar));
    }

    @Test
    void testDeleteById() {
        doNothing().when(lugarRepository).deleteById(1L);

        lugarService.deleteById(1L);

        verify(lugarRepository).deleteById(1L);
    }

    @Test
    void testFindByNombreLugar() {
        when(lugarRepository.findByNombreLugar("Machu Picchu"))
                .thenReturn(Optional.of(lugar));

        Optional<Lugar> result = lugarService.findByNombreLugar("Machu Picchu");

        assertTrue(result.isPresent());
    }

    @Test
    void testFindByCiudad() {
        when(lugarRepository.findByCiudadContainingIgnoreCase("cusco"))
                .thenReturn(List.of(lugar));

        List<Lugar> result = lugarService.findByCiudad("cusco");

        assertEquals(1, result.size());
    }
    
    @Test
    void testFindByNombreLugarContaining() {
        when(lugarRepository.findByNombreLugarContainingIgnoreCase("machu"))
                .thenReturn(List.of(lugar));

        List<Lugar> result = lugarService.findByNombreLugarContaining("machu");

        assertEquals(1, result.size());
    }
  
    @Test
    void testBuscarPorTermino() {
        when(lugarRepository.findByNombreLugarOrCiudadContainingIgnoreCase("cusco"))
                .thenReturn(List.of(lugar));

        List<Lugar> result = lugarService.buscarPorTermino("cusco");

        assertEquals(1, result.size());
    }

    @Test
    void testExistsByNombreLugar() {
        when(lugarRepository.existsByNombreLugar("Machu Picchu"))
                .thenReturn(true);

        boolean exists = lugarService.existsByNombreLugar("Machu Picchu");

        assertTrue(exists);
    }
}