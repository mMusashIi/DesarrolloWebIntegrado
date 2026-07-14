package com.backend.notificacion_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionReservaRequest {
    private String phone;
    private String name;
    private String packageName;
    private LocalDate date;
    private int persons;
    private String reservaId;
}
