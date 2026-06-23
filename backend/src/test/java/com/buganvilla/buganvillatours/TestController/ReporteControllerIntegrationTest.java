package com.buganvilla.buganvillatours.TestController;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.buganvilla.buganvillatours.service.ReporteService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReporteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReporteService reporteService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDescargarExcel() throws Exception {

        // Simular datos generados por el Service
        byte[] excelBytes = "fake-excel-data".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);

        when(reporteService.generarReporteReservasExcel()).thenReturn(inputStream);

        mockMvc.perform(get("/api/reportes/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=reservas.xlsx"))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(content().bytes(excelBytes));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDescargarPDF() throws Exception {

        byte[] pdfBytes = "fake-pdf-data".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);

        when(reporteService.generarReporteReservasPDF()).thenReturn(inputStream);

        mockMvc.perform(get("/api/reportes/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=reservas.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    @WithMockUser // No ADMIN
    void testAccesoDenegadoSinRolAdmin() throws Exception {
        mockMvc.perform(get("/api/reportes/pdf"))
                .andExpect(status().isForbidden());
    }
}
