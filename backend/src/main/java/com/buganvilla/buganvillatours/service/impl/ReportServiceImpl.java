package com.buganvilla.buganvillatours.service.impl;

import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReservaRepository reservaRepository;

    @Override
    public ByteArrayInputStream generarReporteReservasExcel(Long usuarioId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Mis Reservas");

            // Crear header
            String[] headers = { "ID", "Paquete", "Fecha Salida", "Personas", "Estado", "Precio Total",
                    "Fecha Reserva" };
            Row headerRow = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Obtener reservas del usuario
            List<Reserva> reservas = reservaRepository.findByUsuarioIdUsuario(usuarioId);

            // Llenar datos
            int rowNum = 1;
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(reserva.getIdReserva());
                row.createCell(1).setCellValue(reserva.getInventario().getPaquete().getNombrePaquete());
                row.createCell(2).setCellValue(reserva.getInventario().getFechaSalida().toString());
                row.createCell(3).setCellValue(reserva.getCantidadPersonas());
                row.createCell(4).setCellValue(reserva.getEstado());
                row.createCell(5).setCellValue(reserva.getInventario().getPaquete().getPrecioBase()
                        .multiply(new java.math.BigDecimal(reserva.getCantidadPersonas())).doubleValue());
                row.createCell(6).setCellValue(reserva.getFechaReserva().format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Error generando Excel de reservas", e);
            throw new RuntimeException("Error generando reporte Excel");
        }
    }

    @Override
    public ByteArrayInputStream generarReporteReservaPDF(Long reservaId) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // ---- CAMBIO IMPORTANTE: IllegalArgumentException ----
            Reserva reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe la reserva con ID " + reservaId));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Título
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("CONFIRMACIÓN DE RESERVA - BUGANVILLA TOURS");
                contentStream.endText();

                // Contenido
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);

                contentStream.showText("ID Reserva: " + reserva.getIdReserva());
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Paquete: " + reserva.getInventario().getPaquete().getNombrePaquete());
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Fecha de Salida: " + reserva.getInventario().getFechaSalida());
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Personas: " + reserva.getCantidadPersonas());
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Estado: " + reserva.getEstado());
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Precio Total: $" +
                        reserva.getInventario().getPaquete().getPrecioBase()
                                .multiply(new BigDecimal(reserva.getCantidadPersonas())));
                contentStream.newLineAtOffset(0, -20);

                contentStream.showText("Fecha de Reserva: " +
                        reserva.getFechaReserva().format(DateTimeFormatter.ISO_LOCAL_DATE));
                contentStream.endText();

                // Footer
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 100);
                contentStream.showText("Gracias por elegir Buganvilla Tours - ¡Buen viaje!");
                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Error generando PDF de reserva", e);
            throw new RuntimeException("Error generando reporte PDF");
        }
    }

    @Override
    public ByteArrayInputStream generarReporteTodasReservasExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Todas las Reservas");

            // Header similar al anterior pero con más columnas
            String[] headers = { "ID", "Usuario", "Paquete", "Fecha Salida", "Personas", "Estado", "Precio Total",
                    "Fecha Reserva" };
            Row headerRow = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Obtener todas las reservas
            List<Reserva> reservas = reservaRepository.findAll();

            // Llenar datos
            int rowNum = 1;
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(reserva.getIdReserva());
                row.createCell(1)
                        .setCellValue(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido());
                row.createCell(2).setCellValue(reserva.getInventario().getPaquete().getNombrePaquete());
                row.createCell(3).setCellValue(reserva.getInventario().getFechaSalida().toString());
                row.createCell(4).setCellValue(reserva.getCantidadPersonas());
                row.createCell(5).setCellValue(reserva.getEstado());
                row.createCell(6).setCellValue(reserva.getInventario().getPaquete().getPrecioBase()
                        .multiply(new java.math.BigDecimal(reserva.getCantidadPersonas())).doubleValue());
                row.createCell(7).setCellValue(reserva.getFechaReserva().format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Error generando Excel de todas las reservas", e);
            throw new RuntimeException("Error generando reporte Excel completo");
        }
    }
}