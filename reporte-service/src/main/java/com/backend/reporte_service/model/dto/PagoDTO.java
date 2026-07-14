package com.backend.reporte_service.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Long idPago;
    private Long idReserva;
    private BigDecimal monto;
    private String metodo;
    private String estado;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaCreacion;
}
