package com.buganvilla.buganvillatours.model.mapper;

import com.buganvilla.buganvillatours.model.dto.ReservaDTO;
import com.buganvilla.buganvillatours.model.dto.ReservaRequest;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);

    // Entity -> DTO
    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    @Mapping(source = "usuario.email", target = "emailUsuario")
    @Mapping(source = "inventario.idInventario", target = "idInventario")
    @Mapping(source = "inventario.fechaSalida", target = "fechaSalida")
    @Mapping(source = "inventario.fechaRetorno", target = "fechaRetorno")
    @Mapping(source = "inventario.paquete.idPaquete", target = "idPaquete")
    @Mapping(source = "inventario.paquete.nombrePaquete", target = "nombrePaquete")
    @Mapping(source = "inventario.paquete.precioBase", target = "precioBase")
    ReservaDTO toDto(Reserva reserva);

    // List<Entity> -> List<DTO>
    List<ReservaDTO> toDtoList(List<Reserva> reservas);

    // Request -> Entity (para creación)
    @Mapping(target = "idReserva", ignore = true)
    @Mapping(target = "fechaReserva", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "usuario", ignore = true) // Se asigna por separado
    @Mapping(target = "inventario", ignore = true) // Se asigna por separado
    Reserva toEntity(ReservaRequest reservaRequest);
}