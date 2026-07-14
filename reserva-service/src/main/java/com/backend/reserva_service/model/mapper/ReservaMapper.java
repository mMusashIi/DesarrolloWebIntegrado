package com.backend.reserva_service.model.mapper;

import com.backend.reserva_service.model.dto.ReservaDTO;
import com.backend.reserva_service.model.dto.ReservaRequest;
import com.backend.reserva_service.model.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    ReservaDTO toDto(Reserva reserva);

    List<ReservaDTO> toDtoList(List<Reserva> reservas);

    @Mapping(target = "idReserva", ignore = true)
    @Mapping(target = "fechaReserva", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    Reserva toEntity(ReservaRequest request);
}
