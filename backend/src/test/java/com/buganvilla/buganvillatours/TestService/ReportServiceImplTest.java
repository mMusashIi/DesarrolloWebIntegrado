package com.buganvilla.buganvillatours.TestService;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.service.impl.ReportServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Reserva reservaFake;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Carlos");
        usuario.setApellido("Lopez");

        Paquete paquete = new Paquete();
        paquete.setNombrePaquete("Cusco Aventura");
        paquete.setPrecioBase(new BigDecimal("150.00"));

        InventarioPaquete inventario = new InventarioPaquete();
        inventario.setPaquete(paquete);
        inventario.setFechaSalida(LocalDate.of(2025, 10, 10));

        reservaFake = new Reserva();
        reservaFake.setIdReserva(10L);
        reservaFake.setUsuario(usuario);
        reservaFake.setInventario(inventario);
        reservaFake.setCantidadPersonas(2);
        reservaFake.setEstado("CONFIRMADA");
        reservaFake.setFechaReserva(LocalDateTime.of(2025, 1, 1, 0, 0));
    }

    @Test
    void generarReporteReservaPDF_debeCrearPDF() throws IOException {
        when(reservaRepository.findById(10L)).thenReturn(Optional.of(reservaFake));

        ByteArrayInputStream pdfStream = reportService.generarReporteReservaPDF(10L);

        assertNotNull(pdfStream);

        PDDocument document = Loader.loadPDF(pdfStream.readAllBytes());

        assertEquals(1, document.getNumberOfPages());
        document.close();

        verify(reservaRepository, times(1)).findById(10L);
    }
    
    @Test
    void generarReporteReservasExcel_debeCrearExcel() throws IOException {
        List<Reserva> reservas = List.of(reservaFake);
        when(reservaRepository.findByUsuarioIdUsuario(1L)).thenReturn(reservas);

        ByteArrayInputStream excelStream = reportService.generarReporteReservasExcel(1L);

        assertNotNull(excelStream, "El Excel no debe ser nulo");

        XSSFWorkbook workbook = new XSSFWorkbook(excelStream);
        assertEquals("Mis Reservas", workbook.getSheetName(0));
        assertEquals(2, workbook.getSheet("Mis Reservas").getPhysicalNumberOfRows());
        workbook.close();

        verify(reservaRepository, times(1)).findByUsuarioIdUsuario(1L);
    }

    @Test
    void generarReporteTodasReservasExcel_debeCrearExcelCompleto() throws IOException {
        when(reservaRepository.findAll()).thenReturn(List.of(reservaFake));

        ByteArrayInputStream excelStream = reportService.generarReporteTodasReservasExcel();

        assertNotNull(excelStream);

        XSSFWorkbook workbook = new XSSFWorkbook(excelStream);
        assertEquals("Todas las Reservas", workbook.getSheetName(0));
        assertEquals(2, workbook.getSheet("Todas las Reservas").getPhysicalNumberOfRows());
        workbook.close();

        verify(reservaRepository, times(1)).findAll();
    }
}