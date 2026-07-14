package com.backend.auth_service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthDTO {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private String type = "Bearer";
        private UsuarioDTO usuario;

        public LoginResponse(String token, UsuarioDTO usuario) {
            this.token = token;
            this.usuario = usuario;
        }
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        private String nombre;

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
        private String apellido;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Size(max = 150, message = "El email no puede exceder 150 caracteres")
        private String email;

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        private String password;

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "El teléfono debe incluir código de país, por ejemplo +51987654321")
        private String telefono;
        private String nacionalidad;
    }

    @Data
    public static class ProfileUpdateRequest {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        private String nombre;

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        private String apellido;

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\+[1-9]\\d{7,14}$", message = "El teléfono debe incluir código de país, por ejemplo +51987654321")
        private String telefono;

        private String nacionalidad;
        private String dni;
    }
}
