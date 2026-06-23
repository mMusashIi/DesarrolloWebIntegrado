package com.buganvilla.buganvillatours.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaqueteDTO {
    private Long idPaquete;
    private String nombrePaquete;
    private String descripcion;
    private BigDecimal precioBase;
    private Integer duracionDias;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Información del lugar (solo datos básicos)
    private Long idLugar;
    private String nombreLugar;
    private String ciudadLugar;
}
