package com.buganvilla.buganvillatours.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaRequest {

    @NotNull(message = "El ID del inventario es obligatorio")
    private Long idInventario;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "La cantidad de personas debe ser al menos 1")
    private Integer cantidadPersonas;
}
