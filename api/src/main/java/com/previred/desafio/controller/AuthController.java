package com.previred.desafio.controller;

import com.previred.desafio.api.AuthenticationApi;
import com.previred.desafio.dto.generated.AuthRequest;
import com.previred.desafio.dto.generated.AuthResponse;
import com.previred.desafio.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para el manejo de sesiones y JWT")
public class AuthController implements AuthenticationApi {

    private final AuthService authService;

    @Override
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario y devuelve un token JWT",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes", content = @Content)
        }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        // Mapeo simple para mantener el servicio original compatible
        com.previred.desafio.dto.AuthRequest serviceRequest = new com.previred.desafio.dto.AuthRequest(
            authRequest.getUsername(),
            authRequest.getPassword()
        );
        
        var serviceResponse = authService.login(serviceRequest);
        
        AuthResponse response = new AuthResponse();
        response.setToken(serviceResponse.token());
        response.setType("Bearer"); // Default value as it's not in serviceResponse record
        
        return ResponseEntity.ok(response);
    }
}
