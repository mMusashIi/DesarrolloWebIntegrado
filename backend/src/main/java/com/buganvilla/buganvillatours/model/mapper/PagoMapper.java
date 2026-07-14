package com.buganvilla.buganvillatours.model.mapper;

import com.buganvilla.buganvillatours.model.dto.PagoDTO;
import com.buganvilla.buganvillatours.model.dto.PagoRequest;
import com.buganvilla.buganvillatours.model.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    PagoMapper INSTANCE = Mappers.getMapper(PagoMapper.class);

    // Entity -> DTO
    @Mapping(source = "reserva.idReserva", target = "idReserva")
    @Mapping(source = "reserva.estado", target = "estadoReserva")
    @Mapping(source = "reserva.usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "reserva.usuario.nombre", target = "nombreUsuario")
    PagoDTO toDto(Pago pago);

    // List<Entity> -> List<DTO>
    List<PagoDTO> toDtoList(List<Pago> pagos);

    // Request -> Entity (para creación)
    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "reserva", ignore = true) // Se asigna por separado
    Pago toEntity(PagoRequest pagoRequest);
}