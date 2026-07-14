package com.buganvilla.buganvillatours.model.mapper;

import com.buganvilla.buganvillatours.model.dto.InventarioDTO;
import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    InventarioMapper INSTANCE = Mappers.getMapper(InventarioMapper.class);

    // Entity -> DTO
    @Mapping(source = "paquete.idPaquete", target = "idPaquete")
    @Mapping(source = "paquete.nombrePaquete", target = "nombrePaquete")
    @Mapping(source = "paquete.precioBase", target = "precioBase")
    InventarioDTO toDto(InventarioPaquete inventario);

    // List<Entity> -> List<DTO>
    List<InventarioDTO> toDtoList(List<InventarioPaquete> inventarios);

    // DTO -> Entity (para creación/actualización)
    @Mapping(target = "idInventario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paquete", ignore = true) // Se maneja por separado
    InventarioPaquete toEntity(InventarioDTO inventarioDTO);
}
