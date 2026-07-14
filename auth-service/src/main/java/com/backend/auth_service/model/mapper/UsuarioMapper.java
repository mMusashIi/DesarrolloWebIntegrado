package com.backend.auth_service.model.mapper;

import com.backend.auth_service.model.dto.UsuarioDTO;
import com.backend.auth_service.model.dto.UsuarioRequest;
import com.backend.auth_service.model.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    // Entity -> DTO
    UsuarioDTO toDto(Usuario usuario);

    // List<Entity> -> List<DTO>
    List<UsuarioDTO> toDtoList(List<Usuario> usuarios);

    // Request -> Entity (para creación)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "activo", constant = "true")
    @Mapping(target = "rol", defaultExpression = "java(\"cliente\")")
    Usuario toEntity(UsuarioRequest usuarioRequest);

    // Update entity from Request (ignorar campos que no se actualizan)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "rol", ignore = true)
    Usuario toEntityUpdate(UsuarioRequest usuarioRequest);
}
