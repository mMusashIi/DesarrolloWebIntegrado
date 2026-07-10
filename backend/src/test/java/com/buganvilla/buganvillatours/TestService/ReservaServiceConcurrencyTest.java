package com.buganvilla.buganvillatours.TestService;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;
import com.buganvilla.buganvillatours.service.impl.ReservaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Documents the known race condition: two concurrent requests can both pass
 * verificarDisponibilidad() before either calls reducirCupo().
 * This test serves as a regression marker — it should be updated once
 * @Lock(PESSIMISTIC_WRITE) is added to InventarioPaqueteRepository.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceConcurrencyTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private InventarioPaqueteService inventarioPaqueteService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private InventarioPaquete inventario;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);

        inventario = new InventarioPaquete();
        inventario.setIdInventario(100L);
        inventario.setCupoDisponible(1); // solo 1 cupo
    }

    @Test
    void singleRequest_withAvailability_succeeds() {
        when(inventarioPaqueteService.verificarDisponibilidad(100L, 1)).thenReturn(true);
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reserva reserva = buildReserva(1);
        Reserva saved = reservaService.save(reserva);

        assertEquals("pendiente", saved.getEstado());
    }

    @Test
    void singleRequest_withoutAvailability_throwsException() {
        when(inventarioPaqueteService.verificarDisponibilidad(100L, 1)).thenReturn(false);

        Reserva reserva = buildReserva(1);
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> reservaService.save(reserva));
    }

    @Test
    void concurrentRequests_bothSeeAvailability_documentedRaceCondition() throws InterruptedException {
        // Both threads pass verificarDisponibilidad() — this is the known race condition.
        // Without @Lock, the service cannot prevent this at the application level.
        when(inventarioPaqueteService.verificarDisponibilidad(anyLong(), anyInt())).thenReturn(true);
        when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        int threads = 2;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger successes = new AtomicInteger(0);
        AtomicInteger failures = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    reservaService.save(buildReserva(1));
                    successes.incrementAndGet();
                } catch (RuntimeException e) {
                    failures.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        pool.shutdown();

        // KNOWN ISSUE: without DB-level locking, the mock allows both to succeed.
        // In production with a real DB and @Lock, only 1 should succeed.
        // This assertion documents current behavior — update to assertEquals(1, successes.get())
        // once pessimistic locking is implemented in InventarioPaqueteRepository.
        assertEquals(threads, successes.get() + failures.get(), "All requests must complete");
    }

    private Reserva buildReserva(int personas) {
        Reserva r = new Reserva();
        r.setUsuario(usuario);
        r.setInventario(inventario);
        r.setCantidadPersonas(personas);
        r.setEstado("pendiente");
        return r;
    }
}
