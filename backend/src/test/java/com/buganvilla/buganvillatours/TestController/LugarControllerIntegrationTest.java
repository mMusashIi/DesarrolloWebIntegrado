package com.buganvilla.buganvillatours.TestController;

import com.buganvilla.buganvillatours.model.entity.Lugar;
import com.buganvilla.buganvillatours.model.dto.LugarDTO;
import com.buganvilla.buganvillatours.repository.LugarRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LugarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LugarRepository lugarRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        lugarRepository.deleteAll();

        Lugar l = new Lugar();
        l.setNombreLugar("Miraflores");
        l.setCiudad("Lima");
        l.setDescripcion("Zona turística");
        lugarRepository.save(l);
    }
    @Test
    void testGetAllLugaresIntegration() throws Exception {

        mockMvc.perform(get("/api/lugares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].nombreLugar").value("Miraflores"));
    }

    @Test
    void testGetLugarByIdIntegration() throws Exception {

        Lugar lugar = lugarRepository.findAll().get(0);

        mockMvc.perform(get("/api/lugares/" + lugar.getIdLugar()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombreLugar").value("Miraflores"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateLugarIntegration() throws Exception {

        LugarDTO dto = new LugarDTO();
        dto.setNombreLugar("Cusco Centro");
        dto.setCiudad("Cusco");
        dto.setDescripcion("Lugar histórico");

        mockMvc.perform(post("/api/lugares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lugar creado exitosamente"))
                .andExpect(jsonPath("$.data.nombreLugar").value("Cusco Centro"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateLugarIntegration() throws Exception {

        Lugar lugar = lugarRepository.findAll().get(0);

        LugarDTO dto = new LugarDTO();
        dto.setNombreLugar("Miraflores Updated");
        dto.setCiudad("Lima");
        dto.setDescripcion("Actualizado");

        mockMvc.perform(put("/api/lugares/" + lugar.getIdLugar())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lugar actualizado exitosamente"))
                .andExpect(jsonPath("$.data.nombreLugar").value("Miraflores Updated"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteLugarIntegration() throws Exception {

        Lugar lugar = lugarRepository.findAll().get(0);

        mockMvc.perform(delete("/api/lugares/" + lugar.getIdLugar()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Lugar eliminado exitosamente"));
    }
}
