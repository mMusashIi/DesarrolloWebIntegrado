package com.backend.pago_service.model.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PreferenceRequestDTO {
    private Long reservaId;
    private BigDecimal monto;
    private String descripcion;
    private Integer cantidad;
}
