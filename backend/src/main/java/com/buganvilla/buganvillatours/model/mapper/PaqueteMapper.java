package com.buganvilla.buganvillatours.model.mapper;

import com.buganvilla.buganvillatours.model.dto.PaqueteDTO;
import com.buganvilla.buganvillatours.model.dto.PaqueteDetailDTO;
import com.buganvilla.buganvillatours.model.entity.Paquete;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LugarMapper.class})
public interface PaqueteMapper {

    PaqueteMapper INSTANCE = Mappers.getMapper(PaqueteMapper.class);

    // Entity -> DTO básico
    @Mapping(source = "lugar.idLugar", target = "idLugar")
    @Mapping(source = "lugar.nombreLugar", target = "nombreLugar")
    @Mapping(source = "lugar.ciudad", target = "ciudadLugar")
    PaqueteDTO toDto(Paquete paquete);

    // List<Entity> -> List<DTO>
    List<PaqueteDTO> toDtoList(List<Paquete> paquetes);

    // Entity -> DTO detallado (con lugar completo)
    PaqueteDetailDTO toDetailDto(Paquete paquete);

    // List<Entity> -> List<DTO detallado>
    List<PaqueteDetailDTO> toDetailDtoList(List<Paquete> paquetes);

    // DTO -> Entity (para creación/actualización)
    @Mapping(target = "idPaquete", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "lugar", ignore = true) // Se maneja por separado
    Paquete toEntity(PaqueteDTO paqueteDTO);
}
