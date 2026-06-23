package com.buganvilla.buganvillatours.TestController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.buganvilla.buganvillatours.model.mapper.InventarioMapper;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;

import jakarta.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InventarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioPaqueteService inventarioService;

    @Autowired
    private InventarioMapper inventarioMapper;

    @Test
    void testGetAllInventario() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetInventarioDisponible() throws Exception {
        mockMvc.perform(get("/api/inventario/disponible"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetInventarioByPaquete() throws Exception {
        mockMvc.perform(get("/api/inventario/paquete/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInventarioDisponibleByPaquete() throws Exception {
        mockMvc.perform(get("/api/inventario/paquete/1/disponible"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProximasSalidas() throws Exception {
        mockMvc.perform(get("/api/inventario/proximas-salidas"))
                .andExpect(status().isOk());
    }
}
