package com.backend.pago_service.model.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PreferenceResponseDTO {
    private String initPoint;
    private String preferenceId;
    private Long pagoId;
    private Long reservaId;
}
