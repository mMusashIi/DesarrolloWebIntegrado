package com.backend.reserva_service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReservaRequest {
    @NotNull(message = "El ID del inventario es obligatorio")
    private Long idInventario;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "La cantidad de personas debe ser al menos 1")
    private Integer cantidadPersonas;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombreCliente;

    private String nombreComprador;
    @NotBlank(message = "El teléfono del comprador es obligatorio")
    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "El teléfono del comprador debe usar formato internacional")
    private String telefonoComprador;
    private String nombresViajeros;
    private String telefonosViajeros;
    private String dniCliente;

    @Email(message = "El email del cliente no es válido")
    private String emailCliente;

    @NotBlank(message = "El teléfono del cliente es obligatorio")
    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "El teléfono del cliente debe usar formato internacional")
    private String telefonoCliente;
    private Boolean whatsappOptIn = false;
    private String nombrePaquete;
    private String fechaViaje;
}
