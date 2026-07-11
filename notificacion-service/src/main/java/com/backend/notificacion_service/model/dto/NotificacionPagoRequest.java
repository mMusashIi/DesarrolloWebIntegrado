package com.backend.notificacion_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionPagoRequest {
    private String phone;
    private String name;
    private BigDecimal amount;
    private String reservaId;
}
