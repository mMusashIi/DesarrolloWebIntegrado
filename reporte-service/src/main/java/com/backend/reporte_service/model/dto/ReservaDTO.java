package com.backend.reporte_service.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaDTO {
    private Long idReserva;
    private Long idUsuario;
    private String nombreComprador;
    private String telefonoComprador;
    private String nombreCliente;
    private String nombresViajeros;
    private String telefonosViajeros;
    private String dniCliente;
    private String emailCliente;
    private String telefonoCliente;
    private Long idInventario;
    private String nombrePaquete;
    private String fechaViaje;
    private Integer cantidadPersonas;
    private LocalDateTime fechaReserva;
    private String estado;
    private LocalDateTime fechaCreacion;
}
