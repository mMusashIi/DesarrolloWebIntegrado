package com.buganvilla.buganvillatours.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Lugares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lugar")
    @EqualsAndHashCode.Include
    private Long idLugar;

    @Column(name = "nombre_lugar", nullable = false, length = 100)
    private String nombreLugar;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
