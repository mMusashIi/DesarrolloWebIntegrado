package com.backend.notificacion_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionCancelacionRequest {
    private String phone;
    private String name;
    private String packageName;
}
