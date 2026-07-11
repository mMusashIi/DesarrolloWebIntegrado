package com.backend.pago_service.model.mapper;

import com.backend.pago_service.model.dto.PagoDTO;
import com.backend.pago_service.model.dto.PagoRequest;
import com.backend.pago_service.model.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoDTO toDto(Pago pago);

    List<PagoDTO> toDtoList(List<Pago> pagos);

    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "mercadoPagoPreferenceId", ignore = true)
    @Mapping(target = "mercadoPagoPaymentId", ignore = true)
    @Mapping(target = "mercadoPagoStatus", ignore = true)
    Pago toEntity(PagoRequest pagoRequest);
}
