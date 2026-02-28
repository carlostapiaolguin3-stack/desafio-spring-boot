package com.previred.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para crear o actualizar una tarea")
public record TaskRequest(
    @Schema(example = "Finalizar reporte de API", description = "Título de la tarea")
    @NotBlank(message = "El título es obligatorio")
    String title,

    @Schema(example = "Documentar todos los endpoints en Swagger", description = "Descripción detallada")
    String description,

    @Schema(example = "1", description = "ID del estado de la tarea")
    @NotNull(message = "El ID de estado es obligatorio")
    Long statusId
) {}
