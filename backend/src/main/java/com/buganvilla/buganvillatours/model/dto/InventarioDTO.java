package com.buganvilla.buganvillatours.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventarioDTO {
    private Long idInventario;
    private LocalDate fechaSalida;
    private LocalDate fechaRetorno;
    private Integer cupoTotal;
    private Integer cupoDisponible;
    private LocalDateTime fechaCreacion;

    // Información del paquete
    private Long idPaquete;
    private String nombrePaquete;
    private BigDecimal precioBase;
}