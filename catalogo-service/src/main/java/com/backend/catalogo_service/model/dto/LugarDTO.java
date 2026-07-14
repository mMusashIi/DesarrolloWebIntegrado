package com.backend.catalogo_service.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LugarDTO {
    private Long idLugar;
    private String nombreLugar;
    private String ciudad;
    private String descripcion;
    private LocalDateTime fechaCreacion;
}
