package com.buganvilla.buganvillatours.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    @EqualsAndHashCode.Include
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

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

    // ── Campos MercadoPago ──────────────────────────────────────────────────
    /** ID de la preferencia creada en MercadoPago (obtenido al iniciar el pago) */
    @Column(name = "mp_preference_id", length = 100)
    private String mercadoPagoPreferenceId;

    /** ID del pago confirmado por MercadoPago (llega via webhook IPN) */
    @Column(name = "mp_payment_id", length = 50)
    private String mercadoPagoPaymentId;

    /** Estado reportado por MercadoPago: approved | pending | rejected | cancelled */
    @Column(name = "mp_status", length = 30)
    private String mercadoPagoStatus;

    // ── Métodos de negocio ──────────────────────────────────────────────────
    public void procesarPago() {
        this.estado = "completado";
        this.fechaPago = LocalDateTime.now();
        this.reserva.confirmar();
    }

    public void rechazarPago() {
        this.estado = "rechazado";
        this.reserva.cancelar();
    }
}