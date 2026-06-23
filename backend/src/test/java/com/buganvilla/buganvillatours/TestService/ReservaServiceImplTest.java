package com.buganvilla.buganvillatours.TestService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;
import com.buganvilla.buganvillatours.service.impl.ReservaServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private InventarioPaqueteService inventarioPaqueteService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Reserva reserva;
    private Usuario usuario;
    private InventarioPaquete inventario;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setIdUsuario(10L);

        inventario = new InventarioPaquete();
        inventario.setIdInventario(100L);

        reserva = new Reserva();
        reserva.setIdReserva(1L);
        reserva.setUsuario(usuario);
        reserva.setInventario(inventario);
        reserva.setCantidadPersonas(2);
        reserva.setEstado("PENDIENTE");
    }

    @Test
    void testSaveReservaSuccess() {

        when(inventarioPaqueteService.verificarDisponibilidad(100L, 2))
                .thenReturn(true);

        when(reservaRepository.save(any(Reserva.class)))
                .thenReturn(reserva);

        Reserva result = reservaService.save(reserva);

        assertNotNull(result);
        verify(inventarioPaqueteService).reducirCupo(100L, 2);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testSaveReservaSinDisponibilidad() {

        when(inventarioPaqueteService.verificarDisponibilidad(100L, 2))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> reservaService.save(reserva));
        verify(inventarioPaqueteService, never()).reducirCupo(anyLong(), anyInt());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void testUpdateReservaAumentandoCantidad() {

        Reserva reservaExistente = new Reserva();
        reservaExistente.setIdReserva(1L);
        reservaExistente.setUsuario(usuario);
        reservaExistente.setInventario(inventario);
        reservaExistente.setCantidadPersonas(2);

        // La nueva reserva aumenta el cupo de 2 a 4 → diferencia 2
        reserva.setCantidadPersonas(4);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reservaExistente));

        when(inventarioPaqueteService.verificarDisponibilidad(100L, 2))
                .thenReturn(true);

        when(reservaRepository.save(any()))
                .thenReturn(reservaExistente);

        Reserva result = reservaService.update(1L, reserva);

        assertEquals(4, result.getCantidadPersonas());
        verify(inventarioPaqueteService).reducirCupo(100L, 2);
    }

    @Test
    void testUpdateReservaReduciendoCantidad() {

        Reserva reservaExistente = new Reserva();
        reservaExistente.setIdReserva(1L);
        reservaExistente.setUsuario(usuario);
        reservaExistente.setInventario(inventario);
        reservaExistente.setCantidadPersonas(5);

        // la nueva reserva baja de 5 a 3 → diferencia (-2)
        reserva.setCantidadPersonas(3);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reservaExistente));

        when(reservaRepository.save(any()))
                .thenReturn(reservaExistente);

        Reserva result = reservaService.update(1L, reserva);

        assertEquals(3, result.getCantidadPersonas());
        verify(inventarioPaqueteService).aumentarCupo(100L, 2);
    }

    @Test
    void testUpdateReservaNotFound() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> reservaService.update(99L, reserva));
    }

    @Test
    void testDeleteReserva() {

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        reservaService.deleteById(1L);

        verify(inventarioPaqueteService)
                .aumentarCupo(100L, 2);

        verify(reservaRepository)
                .deleteById(1L);
    }

    @Test
    void testDeleteReservaNotFound() {

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.deleteById(1L));
    }

    @Test
    void testCancelarReserva() {
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        when(reservaRepository.save(any()))
                .thenReturn(reserva);

        Reserva result = reservaService.cancelarReserva(1L);

        assertEquals("cancelada", result.getEstado());
    }

    @Test
    void testConfirmarReserva() {
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        when(reservaRepository.save(any()))
                .thenReturn(reserva);

        Reserva result = reservaService.confirmarReserva(1L);

        assertEquals("confirmada", result.getEstado());
    }

    // ---------------------------------------------------------
    // MÉTODOS DE CONSULTA SIMPLE
    // ---------------------------------------------------------

    @Test
    void testFindAll() {
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));
        List<Reserva> result = reservaService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        Optional<Reserva> result = reservaService.findById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByUsuario() {
        when(reservaRepository.findByUsuarioIdUsuario(10L))
                .thenReturn(List.of(reserva));

        List<Reserva> result = reservaService.findByUsuario(10L);
        assertEquals(1, result.size());
    }

    @Test
    void testCountByUsuario() {
        when(reservaRepository.countByUsuarioIdUsuario(10L))
                .thenReturn(5L);

        long count = reservaService.countByUsuario(10L);
        assertEquals(5, count);
    }
}
