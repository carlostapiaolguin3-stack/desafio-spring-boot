package com.previred.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Detalles de la tarea")
public record TaskDTO(
    @Schema(example = "1")
    Long id,
    
    @Schema(example = "Finalizar reporte de API")
    String title,
    
    @Schema(example = "Documentar todos los endpoints en Swagger")
    String description,
    
    @Schema(example = "PENDIENTE")
    String statusName,
    
    @Schema(example = "admin")
    String username,
    
    @Schema(example = "2024-03-25T10:00:00")
    LocalDateTime createdAt,
    
    @Schema(example = "2024-03-25T10:30:00")
    LocalDateTime updatedAt
) {}
