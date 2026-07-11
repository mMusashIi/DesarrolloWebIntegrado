package com.backend.catalogo_service.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaqueteDetailDTO {
    private Long idPaquete;
    private String nombrePaquete;
    private String descripcion;
    private BigDecimal precioBase;
    private Integer duracionDias;
    private String estado;
    private String imagenUrl;
    private LocalDateTime fechaCreacion;

    // Información completa del lugar
    private LugarDTO lugar;
}
