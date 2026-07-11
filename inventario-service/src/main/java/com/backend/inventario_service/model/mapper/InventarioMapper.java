package com.backend.inventario_service.model.mapper;

import com.backend.inventario_service.model.dto.InventarioDTO;
import com.backend.inventario_service.model.entity.InventarioPaquete;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventarioMapper {

    InventarioMapper INSTANCE = Mappers.getMapper(InventarioMapper.class);

    // Entity -> DTO
    InventarioDTO toDto(InventarioPaquete inventario);

    // List<Entity> -> List<DTO>
    List<InventarioDTO> toDtoList(List<InventarioPaquete> inventarios);

    // DTO -> Entity (para creación/actualización)
    @Mapping(target = "idInventario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    InventarioPaquete toEntity(InventarioDTO inventarioDTO);
}
