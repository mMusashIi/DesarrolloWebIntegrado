package com.backend.inventario_service.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventarioDTO {
    private Long idInventario;
    private Long idPaquete;
    private LocalDate fechaSalida;
    private LocalDate fechaRetorno;
    private Integer cupoTotal;
    private Integer cupoDisponible;
    private LocalDateTime fechaCreacion;
}
