package com.buganvilla.buganvillatours.TestController;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.buganvilla.buganvillatours.model.dto.PaqueteDTO;
import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.repository.PaqueteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaqueteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long paqueteId;

    @BeforeEach
    void setup() {
        paqueteRepository.deleteAll();

        Paquete p = new Paquete();
        p.setNombrePaquete("Paquete Cusco");
        p.setDescripcion("Tour 3 días");
        p.setPrecioBase(new BigDecimal("350.00"));
        p.setEstado("activo");

        paqueteId = paqueteRepository.save(p).getIdPaquete();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPaquetes() throws Exception {
        mockMvc.perform(get("/api/paquetes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nombrePaquete").value("Paquete Cusco"));
    }

    @Test
    void testGetPaquetesActivos() throws Exception {
        mockMvc.perform(get("/api/paquetes/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].estado").value("activo"));
    }

    @Test
    void testGetPaqueteById() throws Exception {
        mockMvc.perform(get("/api/paquetes/" + paqueteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombrePaquete").value("Paquete Cusco"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePaquete() throws Exception {

        PaqueteDTO dto = new PaqueteDTO();
        dto.setNombrePaquete("Nuevo Paquete Lima");
        dto.setDescripcion("Tour 2 días");
        dto.setPrecioBase(new BigDecimal("200.00"));
        dto.setEstado("activo");

        mockMvc.perform(post("/api/paquetes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombrePaquete").value("Nuevo Paquete Lima"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePaquete() throws Exception {

        PaqueteDTO dto = new PaqueteDTO();
        dto.setNombrePaquete("Paquete Cusco Modificado");
        dto.setDescripcion("Tour actualizado");
        dto.setPrecioBase(new BigDecimal("400.00"));
        dto.setEstado("activo");

        mockMvc.perform(put("/api/paquetes/" + paqueteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombrePaquete").value("Paquete Cusco Modificado"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletePaquete() throws Exception {
        mockMvc.perform(delete("/api/paquetes/" + paqueteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertEquals(0, paqueteRepository.count());
    }

    @Test
    void testGetPaquetesActivosPublicos() throws Exception {
        mockMvc.perform(get("/api/paquetes/public/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetPaquetePublico() throws Exception {
        mockMvc.perform(get("/api/paquetes/public/" + paqueteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombrePaquete").value("Paquete Cusco"));
    }

    @Test
    void testBuscarPaquetes() throws Exception {
        mockMvc.perform(get("/api/paquetes/public/buscar")
                .param("nombre", "Cusco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSearchPaquetesAlias() throws Exception {
        mockMvc.perform(get("/api/paquetes/public/search")
                .param("nombre", "Cusco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
