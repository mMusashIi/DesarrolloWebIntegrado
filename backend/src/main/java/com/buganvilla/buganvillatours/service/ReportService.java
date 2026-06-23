package com.buganvilla.buganvillatours.service;

import java.io.ByteArrayInputStream;

public interface ReportService {

    // Generar Excel con reservas de un usuario
    ByteArrayInputStream generarReporteReservasExcel(Long usuarioId);

    // Generar PDF con reserva específica (para ticket/confirmación)
    ByteArrayInputStream generarReporteReservaPDF(Long reservaId);

    // Generar Excel con todas las reservas (admin)
    ByteArrayInputStream generarReporteTodasReservasExcel();
}
