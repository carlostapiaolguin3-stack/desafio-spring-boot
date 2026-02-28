package com.previred.desafio.mapper;

import com.previred.desafio.dto.TaskDTO;
import com.previred.desafio.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper generado por MapStruct en tiempo de compilacion.
 * Convierte la entidad Task al DTO TaskDTO para la capa de presentacion.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "status.name", target = "statusName")
    @Mapping(source = "user.username", target = "username")
    TaskDTO toDTO(Task task);
}
