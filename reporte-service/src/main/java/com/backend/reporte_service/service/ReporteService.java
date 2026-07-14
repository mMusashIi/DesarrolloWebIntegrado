package com.backend.reporte_service.service;

import com.backend.reporte_service.client.ReservaClient;
import com.backend.reporte_service.model.dto.ReservaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReservaClient reservaClient;

    public ByteArrayInputStream generarReporteReservasExcel(String jwtToken) throws IOException {
        List<ReservaDTO> reservas = reservaClient.getAllReservas(jwtToken);
        String[] columns = {"Código", "Comprador", "Celular comprador", "Cliente principal", "Viajeros / clientes",
                "Celulares viajeros", "Paquete", "Fecha de viaje", "Personas", "Fecha de compra", "Estado"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reservas");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (ReservaDTO reserva : reservas) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("RES-" + (reserva.getIdReserva() != null ? reserva.getIdReserva() : 0));
                row.createCell(1).setCellValue(texto(reserva.getNombreComprador()));
                row.createCell(2).setCellValue(texto(reserva.getTelefonoComprador()));
                row.createCell(3).setCellValue(texto(reserva.getNombreCliente()));
                row.createCell(4).setCellValue(texto(reserva.getNombresViajeros()));
                row.createCell(5).setCellValue(texto(reserva.getTelefonosViajeros()));
                row.createCell(6).setCellValue(texto(reserva.getNombrePaquete()));
                row.createCell(7).setCellValue(texto(reserva.getFechaViaje()));
                row.createCell(8).setCellValue(reserva.getCantidadPersonas() != null ? reserva.getCantidadPersonas() : 0);
                row.createCell(9).setCellValue(reserva.getFechaReserva() != null ? reserva.getFechaReserva().toString() : "");
                row.createCell(10).setCellValue(texto(reserva.getEstado()));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteReservasPDF(String jwtToken) throws IOException {
        List<ReservaDTO> reservas = reservaClient.getAllReservas(jwtToken);

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Reporte de Reservas - Buganvilla Tours");
            contentStream.endText();

            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);

            int yPosition = 720;
            int margin = 25;

            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Compras por comprador y clientes del servicio");
            contentStream.endText();
            yPosition -= 20;

            for (ReservaDTO reserva : reservas) {
                if (yPosition < 100) {
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                    yPosition = 750;
                }

                String line = String.format("RES-%d | Comprador: %s | Cel: %s | Estado: %s",
                        reserva.getIdReserva() != null ? reserva.getIdReserva() : 0,
                        limitar(texto(reserva.getNombreComprador()), 28),
                        limitar(texto(reserva.getTelefonoComprador()), 16),
                        texto(reserva.getEstado()));

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(line);
                contentStream.endText();
                yPosition -= 15;

                String detail = String.format("Paquete: %s | Viaje: %s | Viajeros: %s",
                        limitar(texto(reserva.getNombrePaquete()), 25),
                        limitar(texto(reserva.getFechaViaje()), 12),
                        limitar(texto(reserva.getNombresViajeros()), 45));
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 12, yPosition);
                contentStream.showText(detail);
                contentStream.endText();
                yPosition -= 20;

                String phones = "Celulares de viajeros: " + limitar(texto(reserva.getTelefonosViajeros()), 70);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin + 12, yPosition);
                contentStream.showText(phones);
                contentStream.endText();
                yPosition -= 20;
            }

            contentStream.close();
            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private String texto(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private String limitar(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max - 3) + "...";
    }
}
