package com.previred.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de autenticación")
public record AuthRequest(
    @Schema(example = "admin", description = "Nombre de usuario")
    @NotBlank(message = "El nombre de usuario es obligatorio")
    String username,

    @Schema(example = "admin", description = "Contraseña")
    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
