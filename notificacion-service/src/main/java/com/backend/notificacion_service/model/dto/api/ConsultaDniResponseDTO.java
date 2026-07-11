package com.backend.notificacion_service.model.dto.api;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDniResponseDTO {
    @JsonAlias("first_name")
    private String nombres;
    @JsonAlias("first_last_name")
    private String apellidoPaterno;
    @JsonAlias("second_last_name")
    private String apellidoMaterno;
    private String tipoDocumento;
    @JsonAlias("document_number")
    private String numeroDocumento;
    private String digitoVerificador;
}
