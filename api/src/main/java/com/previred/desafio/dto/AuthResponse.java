package com.previred.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticación con token JWT")
public record AuthResponse(
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
    String token,

    @Schema(description = "Nombre de usuario", example = "admin")
    String username,

    @Schema(description = "Rol del usuario", example = "ROLE_ADMIN")
    String role
) {}
