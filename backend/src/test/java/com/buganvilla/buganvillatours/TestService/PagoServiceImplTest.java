package com.buganvilla.buganvillatours.TestService;

import org.junit.jupiter.api.BeforeEach;

import com.buganvilla.buganvillatours.model.entity.Pago;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.repository.PagoRepository;
import com.buganvilla.buganvillatours.service.ReservaService;
import com.buganvilla.buganvillatours.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Pago crearPago() {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(1L);

        return Pago.builder()
                .idPago(1L)
                .monto(BigDecimal.valueOf(100))
                .metodo("YAPE")
                .estado("pendiente")
                .reserva(reserva)
                .fechaPago(LocalDateTime.now())
                .build();
    }

    @Test
    void testFindAll() {
        when(pagoRepository.findAll()).thenReturn(List.of(crearPago()));

        List<Pago> result = pagoService.findAll();

        assertEquals(1, result.size());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(crearPago()));

        Optional<Pago> result = pagoService.findById(1L);

        assertTrue(result.isPresent());
        verify(pagoRepository).findById(1L);
    }

    @Test
    void testSave() {
        Pago pago = crearPago();

        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago result = pagoService.save(pago);

        assertNotNull(result);
        assertEquals(1L, result.getIdPago());
        verify(pagoRepository).save(pago);
    }

    @Test
    void testUpdate() {
        Pago pagoExistente = crearPago();
        Pago pagoActualizado = crearPago();
        pagoActualizado.setMonto(BigDecimal.valueOf(200));

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoExistente));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoExistente);

        Pago result = pagoService.update(1L, pagoActualizado);

        assertEquals(BigDecimal.valueOf(200), result.getMonto());
        verify(pagoRepository).save(pagoExistente);
    }

    @Test
    void testDeleteById() {
        pagoService.deleteById(1L);
        verify(pagoRepository).deleteById(1L);
    }

    @Test
    void testFindByEstado() {
        when(pagoRepository.findByEstado("pendiente"))
                .thenReturn(List.of(crearPago()));

        List<Pago> result = pagoService.findByEstado("pendiente");

        assertEquals(1, result.size());
        verify(pagoRepository).findByEstado("pendiente");
    }

    @Test
    void testFindByMetodo() {
        when(pagoRepository.findByMetodo("YAPE"))
                .thenReturn(List.of(crearPago()));

        List<Pago> result = pagoService.findByMetodo("YAPE");

        assertEquals(1, result.size());
        verify(pagoRepository).findByMetodo("YAPE");
    }

    @Test
    void testFindByRangoFechas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();

        when(pagoRepository.findByFechaPagoBetween(inicio, fin))
                .thenReturn(List.of(crearPago()));

        List<Pago> result = pagoService.findByRangoFechas(inicio, fin);

        assertEquals(1, result.size());
        verify(pagoRepository).findByFechaPagoBetween(inicio, fin);
    }

    @Test
    void testFindUltimoPagoByReserva() {
        when(pagoRepository.findFirstByReservaIdReservaOrderByFechaPagoDesc(1L))
                .thenReturn(Optional.of(crearPago()));

        Optional<Pago> result = pagoService.findUltimoPagoByReserva(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testProcesarPago() {
        Reserva reserva = new Reserva();
        Pago pago = Pago.builder()
                .idPago(1L)
                .estado("pendiente")
                .reserva(reserva)
                .build();

        // El pago procesado esperado
        Pago pagoProcesado = Pago.builder()
                .idPago(1L)
                .estado("completado")
                .reserva(reserva)
                .build();

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoProcesado);

        Pago result = pagoService.procesarPago(1L);

        assertEquals("completado", result.getEstado());
        verify(pagoRepository).findById(1L);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void testRechazarPago() {
        Pago pago = crearPago();

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago result = pagoService.rechazarPago(1L);

        assertEquals("rechazado", result.getEstado());
        verify(pagoRepository).save(pago);
    }

    @Test
    void testTotalPagosCompletados() {
        when(pagoRepository.findTotalPagosCompletadosPorPeriodo(any(), any()))
                .thenReturn(Optional.of(BigDecimal.valueOf(500)));

        BigDecimal total = pagoService.getTotalPagosCompletadosPorPeriodo(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now());

        assertEquals(BigDecimal.valueOf(500), total);
    }
}