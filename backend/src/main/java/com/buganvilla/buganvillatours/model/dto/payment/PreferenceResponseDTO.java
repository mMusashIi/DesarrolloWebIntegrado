package com.buganvilla.buganvillatours.model.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceResponseDTO {
    /** URL de pago de MercadoPago (sandbox o producción según configuración) */
    private String initPoint;

    /** ID de la preferencia en MercadoPago */
    private String preferenceId;

    /** ID del registro Pago creado en la base de datos local */
    private Long pagoId;

    /** ID de la reserva asociada */
    private Long reservaId;
}
