package com.buganvilla.buganvillatours.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    @EqualsAndHashCode.Include
    private Long idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventario", nullable = false)
    private InventarioPaquete inventario;

    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    @Column(name = "fecha_reserva")
    @Builder.Default
    private LocalDateTime fechaReserva = LocalDateTime.now();

    @Column(name = "estado", length = 20)
    @Builder.Default
    private String estado = "pendiente";

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public void cancelar() {
        this.estado = "cancelada";
        this.inventario.aumentarCupo(this.cantidadPersonas);
    }

    public void confirmar() {
        this.estado = "confirmada";
    }
}
