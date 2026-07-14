package com.backend.pago_service.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pagos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    @EqualsAndHashCode.Include
    private Long idPago;

    @Column(name = "id_reserva", nullable = false)
    private Long idReserva;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "metodo", length = 30)
    private String metodo;

    @Column(name = "estado", length = 20)
    @Builder.Default
    private String estado = "pendiente";

    @Column(name = "fecha_pago")
    @Builder.Default
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "mp_preference_id", length = 100)
    private String mercadoPagoPreferenceId;

    @Column(name = "mp_payment_id", length = 50)
    private String mercadoPagoPaymentId;

    @Column(name = "mp_status", length = 30)
    private String mercadoPagoStatus;

    public void procesarPago() {
        this.estado = "completado";
        this.fechaPago = LocalDateTime.now();
    }

    public void rechazarPago() {
        this.estado = "rechazado";
    }
}
