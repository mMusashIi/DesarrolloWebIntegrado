package com.buganvilla.buganvillatours.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservaDTO {
    private Long idReserva;
    private Integer cantidadPersonas;
    private LocalDateTime fechaReserva;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Información del usuario (solo datos básicos)
    private Long idUsuario;
    private String nombreUsuario;
    private String emailUsuario;

    // Información del inventario
    private Long idInventario;
    private LocalDate fechaSalida;
    private LocalDate fechaRetorno;

    // Información del paquete
    private Long idPaquete;
    private String nombrePaquete;
    private BigDecimal precioBase;

    // Calculado: precio total
    public BigDecimal getPrecioTotal() {
        return precioBase != null ? precioBase.multiply(BigDecimal.valueOf(cantidadPersonas)) : BigDecimal.ZERO;
    }
}
