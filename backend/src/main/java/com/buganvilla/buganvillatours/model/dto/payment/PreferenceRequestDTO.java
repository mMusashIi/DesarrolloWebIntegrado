package com.buganvilla.buganvillatours.model.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceRequestDTO {
    /** ID de la reserva para la que se genera el pago */
    private Long reservaId;
}
