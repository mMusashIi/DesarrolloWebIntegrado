package com.buganvilla.buganvillatours.TestController;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.buganvilla.buganvillatours.model.dto.ReservaRequest;
import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.repository.InventarioPaqueteRepository;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.repository.UsuarioRepository;
import com.buganvilla.buganvillatours.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InventarioPaqueteRepository inventarioPaqueteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityUtil securityUtil;

    private Usuario usuario;
    private InventarioPaquete inventario;

    @BeforeEach
    void setup() {
        reservaRepository.deleteAll();
        inventarioPaqueteRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario
        usuario = new Usuario();
        usuario.setEmail("cliente@mail.com");
        usuario.setPassword("12345");
        usuario = usuarioRepository.save(usuario);

        // Crear inventario
        inventario = new InventarioPaquete();
        inventario.setCupoDisponible(10);
        inventario = inventarioPaqueteRepository.save(inventario);

        // Mockear usuario actual
        Mockito.when(securityUtil.getCurrentUser()).thenReturn(Optional.of(usuario));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Order(1)
    void testGetAllReservas() throws Exception {

        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "cliente@mail.com")
    @Order(2)
    void testCreateReserva() throws Exception {

        ReservaRequest request = new ReservaRequest();
        request.setIdInventario(inventario.getIdInventario());
        request.setCantidadPersonas(3);

        mockMvc.perform(post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reserva creada exitosamente"))
                .andExpect(jsonPath("$.data.cantidadPersonas").value(3));
    }

    @Test
    @WithMockUser(username = "cliente@mail.com")
    @Order(3)
    void testGetMisReservas() throws Exception {

        // Crear reserva directamente en BD
        Reserva r = Reserva.builder()
                .usuario(usuario)
                .inventario(inventario)
                .cantidadPersonas(2)
                .estado("pendiente")
                .build();

        reservaRepository.save(r);

        mockMvc.perform(get("/api/reservas/mis-reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].cantidadPersonas").value(2));
    }

    @Test
    @WithMockUser(username = "cliente@mail.com")
    @Order(4)
    void testCancelarReserva() throws Exception {

        Reserva r = Reserva.builder()
                .usuario(usuario)
                .inventario(inventario)
                .cantidadPersonas(2)
                .estado("pendiente")
                .build();

        r = reservaRepository.save(r);

        mockMvc.perform(put("/api/reservas/" + r.getIdReserva() + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reserva cancelada exitosamente"))
                .andExpect(jsonPath("$.data.estado").value("cancelada"));
    }
}
