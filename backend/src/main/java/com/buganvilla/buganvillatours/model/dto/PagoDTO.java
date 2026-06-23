package com.buganvilla.buganvillatours.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Long idPago;
    private BigDecimal monto;
    private String metodo;
    private String estado;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaCreacion;

    // Información de la reserva
    private Long idReserva;
    private String estadoReserva;

    // Información del usuario
    private Long idUsuario;
    private String nombreUsuario;
}