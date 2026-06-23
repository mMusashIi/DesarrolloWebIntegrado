package com.buganvilla.buganvillatours.TestController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.buganvilla.buganvillatours.model.entity.Pago;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.mapper.PagoMapper;
import com.buganvilla.buganvillatours.service.PagoService;
import com.buganvilla.buganvillatours.service.ReservaService;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class PagoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoMapper pagoMapper;

    @Autowired
    private ReservaService reservaService;

    private Pago crearPagoReal() {
        Reserva reserva = new Reserva();
        reserva.setEstado("pendiente");
        reserva.setCantidadPersonas(1);
        reserva = reservaService.save(reserva);

        Pago pago = new Pago();
        pago.setReserva(reserva);
        pago.setEstado("pendiente");
        pago.setMonto(BigDecimal.TEN);

        return pagoService.save(pago);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPagos_realIntegracion() throws Exception {

        crearPagoReal();

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetPagosByReserva_realIntegracion() throws Exception {

        Pago pago = crearPagoReal();

        mockMvc.perform(get("/api/pagos/reserva/" + pago.getReserva().getIdReserva()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testCrearPago_realIntegracion() throws Exception {

        String json = """
            {
              "monto": 20.50,
              "metodoPago": "YAPE",
              "idReserva": 1
            }
            """;

        mockMvc.perform(post("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testProcesarPago_realIntegracion() throws Exception {

        Pago pago = crearPagoReal();

        mockMvc.perform(put("/api/pagos/" + pago.getIdPago() + "/procesar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
