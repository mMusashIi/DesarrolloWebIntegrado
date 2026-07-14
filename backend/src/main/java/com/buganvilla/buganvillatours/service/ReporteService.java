package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReservaRepository reservaRepository;

    public ByteArrayInputStream generarReporteReservasExcel() throws IOException {
        List<Reserva> reservas = reservaRepository.findAll();
        String[] columns = {"ID", "Cliente", "Paquete", "Fecha Viaje", "Personas", "Fecha Reserva", "Estado"};

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reservas");

            // Header Font
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            // Header CellStyle
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Row for Header
            Row headerRow = sheet.createRow(0);

            // Header
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // CellStyle for dates
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd-MM-yyyy HH:mm"));

            int rowIdx = 1;
            for (Reserva reserva : reservas) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(reserva.getIdReserva());
                row.createCell(1).setCellValue(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido());
                row.createCell(2).setCellValue(reserva.getInventario().getPaquete().getNombrePaquete());
                row.createCell(3).setCellValue(reserva.getInventario().getFechaSalida().toString());
                row.createCell(4).setCellValue(reserva.getCantidadPersonas());
                
                Cell dateCell = row.createCell(5);
                dateCell.setCellValue(reserva.getFechaReserva().toString());
                // dateCell.setCellStyle(dateCellStyle); // Simplificado para evitar problemas de conversión

                row.createCell(6).setCellValue(reserva.getEstado());
            }
            
            // Auto-size columns
            for(int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream generarReporteReservasPDF() throws IOException {
        List<Reserva> reservas = reservaRepository.findAll();
        
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
            int rowsPerPage = 35;
            int rowCount = 0;

            // Headers
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("ID   | Cliente                | Paquete                | Personas | Estado");
            contentStream.endText();
            yPosition -= 20;

            for (Reserva reserva : reservas) {
                if (yPosition < 50) {
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                    yPosition = 750;
                }

                String line = String.format("%-4d | %-20s | %-20s | %-8d | %s",
                        reserva.getIdReserva(),
                        truncate(reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido(), 20),
                        truncate(reserva.getInventario().getPaquete().getNombrePaquete(), 20),
                        reserva.getCantidadPersonas(),
                        reserva.getEstado());

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(line);
                contentStream.endText();
                yPosition -= 15;
            }

            contentStream.close();
            document.save(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }
}
