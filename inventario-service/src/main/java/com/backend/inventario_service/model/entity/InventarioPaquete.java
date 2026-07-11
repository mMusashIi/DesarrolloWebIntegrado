package com.backend.inventario_service.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Inventario_Paquetes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InventarioPaquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    @EqualsAndHashCode.Include
    private Long idInventario;

    @Column(name = "id_paquete", nullable = false)
    private Long idPaquete;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "fecha_retorno")
    private LocalDate fechaRetorno;

    @Column(name = "cupo_total", nullable = false)
    private Integer cupoTotal;

    @Column(name = "cupo_disponible", nullable = false)
    @Builder.Default
    private Integer cupoDisponible = 0;

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public boolean tieneCupoDisponible(Integer cantidad) {
        return cupoDisponible >= cantidad;
    }

    public void reducirCupo(Integer cantidad) {
        if (tieneCupoDisponible(cantidad)) {
            this.cupoDisponible -= cantidad;
        } else {
            throw new IllegalStateException("No hay cupo disponible suficiente");
        }
    }

    public void aumentarCupo(Integer cantidad) {
        this.cupoDisponible += cantidad;
    }
}
