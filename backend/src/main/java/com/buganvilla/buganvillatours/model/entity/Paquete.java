package com.buganvilla.buganvillatours.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Paquetes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paquete")
    @EqualsAndHashCode.Include
    private Long idPaquete;

    @Column(name = "nombre_paquete", nullable = false, length = 150)
    private String nombrePaquete;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "estado", length = 20)
    @Builder.Default
    private String estado = "activo";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lugar")
    private Lugar lugar;

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
