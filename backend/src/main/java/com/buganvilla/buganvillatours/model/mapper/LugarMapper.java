package com.buganvilla.buganvillatours.model.mapper;

import com.buganvilla.buganvillatours.model.dto.LugarDTO;
import com.buganvilla.buganvillatours.model.entity.Lugar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LugarMapper {

    LugarMapper INSTANCE = Mappers.getMapper(LugarMapper.class);

    // Entity -> DTO
    LugarDTO toDto(Lugar lugar);

    // List<Entity> -> List<DTO>
    List<LugarDTO> toDtoList(List<Lugar> lugares);

    // DTO -> Entity (para creación/actualización)
    @Mapping(target = "idLugar", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Lugar toEntity(LugarDTO lugarDTO);
}