package com.buganvilla.buganvillatours.TestService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.repository.InventarioPaqueteRepository;
import com.buganvilla.buganvillatours.service.impl.InventarioPaqueteServiceImpl;

@ExtendWith(MockitoExtension.class)
class InventarioPaqueteServiceImplTest {

    @Mock
    private InventarioPaqueteRepository inventarioPaqueteRepository;

    @InjectMocks
    private InventarioPaqueteServiceImpl inventarioService;

    private InventarioPaquete inventario;
    private Paquete paquete;

    @BeforeEach
    void setUp() {
        paquete = new Paquete();
        paquete.setIdPaquete(1L);

        inventario = new InventarioPaquete();
        inventario.setIdInventario(10L);
        inventario.setPaquete(paquete);
        inventario.setFechaSalida(LocalDate.now());
        inventario.setCupoTotal(20);
        inventario.setCupoDisponible(10);
    }

    @Test
    void testSaveNuevoInventario() {
        // solo stubs necesarios para este test
        when(inventarioPaqueteRepository
                .findByPaqueteIdPaqueteAndFechaSalida(1L, inventario.getFechaSalida()))
                .thenReturn(Optional.empty());

        // usamos any() para evitar problemas por igualdad de instancia
        when(inventarioPaqueteRepository.save(any(InventarioPaquete.class)))
                .thenReturn(inventario);

        InventarioPaquete resultado = inventarioService.save(inventario);

        assertEquals(inventario, resultado);
        verify(inventarioPaqueteRepository).save(any(InventarioPaquete.class));
    }

    @Test
    void testSaveInventarioDuplicadoDebeFallar() {
        when(inventarioPaqueteRepository
                .findByPaqueteIdPaqueteAndFechaSalida(1L, inventario.getFechaSalida()))
                .thenReturn(Optional.of(inventario));

        assertThrows(RuntimeException.class,
                () -> inventarioService.save(inventario));

        verify(inventarioPaqueteRepository, never()).save(any());
    }

    @Test
    void testUpdateOk() {
        when(inventarioPaqueteRepository.findById(10L))
                .thenReturn(Optional.of(inventario));

        when(inventarioPaqueteRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0)); // devuelve la entidad modificada

        InventarioPaquete nuevo = new InventarioPaquete();
        nuevo.setFechaSalida(LocalDate.now().plusDays(1));
        nuevo.setFechaRetorno(LocalDate.now().plusDays(2));
        nuevo.setCupoTotal(30);
        nuevo.setCupoDisponible(20);

        InventarioPaquete result = inventarioService.update(10L, nuevo);

        assertEquals(30, result.getCupoTotal());
        assertEquals(20, result.getCupoDisponible());
        verify(inventarioPaqueteRepository).findById(10L);
        verify(inventarioPaqueteRepository).save(any());
    }

    @Test
    void testUpdateInventarioNoExiste() {
        when(inventarioPaqueteRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> inventarioService.update(10L, inventario));
    }

    @Test
    void testDeleteById() {
        // deleteById no requiere stubbing, solo verificar que se invoque
        inventarioService.deleteById(10L);
        verify(inventarioPaqueteRepository).deleteById(10L);
    }

    @Test
    void testVerificarDisponibilidadTrue() {
        when(inventarioPaqueteRepository.findCupoDisponibleById(10L)).thenReturn(10);
        boolean disponible = inventarioService.verificarDisponibilidad(10L, 5);
        assertTrue(disponible);
    }

    @Test
    void testVerificarDisponibilidadFalse() {
        when(inventarioPaqueteRepository.findCupoDisponibleById(10L)).thenReturn(3);
        boolean disponible = inventarioService.verificarDisponibilidad(10L, 5);
        assertFalse(disponible);
    }

    @Test
    void testReducirCupoOk() {
        when(inventarioPaqueteRepository.findById(10L)).thenReturn(Optional.of(inventario));
        when(inventarioPaqueteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        inventarioService.reducirCupo(10L, 5);

        assertEquals(5, inventario.getCupoDisponible());
        verify(inventarioPaqueteRepository).save(any(InventarioPaquete.class));
    }

    @Test
    void testReducirCupoNoSuficienteDebeFallar() {
        inventario.setCupoDisponible(3);
        when(inventarioPaqueteRepository.findById(10L)).thenReturn(Optional.of(inventario));

        assertThrows(RuntimeException.class,
                () -> inventarioService.reducirCupo(10L, 5));

        verify(inventarioPaqueteRepository, never()).save(any());
    }

    @Test
    void testAumentarCupo() {
        when(inventarioPaqueteRepository.findById(10L)).thenReturn(Optional.of(inventario));
        when(inventarioPaqueteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        inventarioService.aumentarCupo(10L, 5);

        assertEquals(15, inventario.getCupoDisponible());
        verify(inventarioPaqueteRepository).save(any(InventarioPaquete.class));
    }
}
