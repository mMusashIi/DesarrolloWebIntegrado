package com.backend.reserva_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reservas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    @EqualsAndHashCode.Include
    private Long idReserva;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "nombre_comprador", length = 250)
    private String nombreComprador;

    @Column(name = "telefono_comprador", length = 30)
    private String telefonoComprador;

    @Column(name = "nombre_cliente", length = 250)
    private String nombreCliente;

    @Lob
    @Column(name = "nombres_viajeros")
    private String nombresViajeros;

    @Lob
    @Column(name = "telefonos_viajeros")
    private String telefonosViajeros;

    @Column(name = "dni_cliente", length = 20)
    private String dniCliente;

    @Column(name = "email_cliente", length = 150)
    private String emailCliente;

    @Column(name = "telefono_cliente", length = 30)
    private String telefonoCliente;

    @Column(name = "whatsapp_opt_in")
    @Builder.Default
    private Boolean whatsappOptIn = false;

    @Column(name = "id_inventario", nullable = false)
    private Long idInventario;

    @Column(name = "nombre_paquete", length = 150)
    private String nombrePaquete;

    @Column(name = "fecha_viaje", length = 30)
    private String fechaViaje;

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
    }

    public void confirmar() {
        this.estado = "confirmada";
    }
}
