package com.previred.desafio.controller;

import com.previred.desafio.api.TaskApi;
import com.previred.desafio.dto.generated.GetAllTasks200Response;
import com.previred.desafio.dto.generated.TaskDTO;
import com.previred.desafio.dto.generated.TaskRequest;
import com.previred.desafio.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "Endpoints para la gestión de tareas (CRUD)")
@SecurityRequirement(name = "bearerAuth")
public class TaskController implements TaskApi {

    private final TaskService taskService;

    @Override
    @Operation(
        summary = "Obtener todas las tareas",
        description = "Devuelve una página de tareas con soporte para filtrado opcional por estado, paginación y ordenamiento",
        responses = {
            @ApiResponse(responseCode = "200", description = "Página de tareas recuperada")
        }
    )
    @GetMapping
    public ResponseEntity<GetAllTasks200Response> getAllTasks(
        @Parameter(description = "Nombre del estado para filtrar (opcional)") @RequestParam(required = false) String status,
        @Parameter(description = "Número de página") @RequestParam(required = false, defaultValue = "0") Integer page,
        @Parameter(description = "Tamaño de página") @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.previred.desafio.dto.TaskDTO> servicePage = taskService.getAllTasks(status, pageable);
        
        GetAllTasks200Response response = new GetAllTasks200Response();
        response.setTotalElements((int) servicePage.getTotalElements());
        response.setTotalPages(servicePage.getTotalPages());
        response.setContent(servicePage.getContent().stream()
                .map(this::mapToApiDTO)
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(response);
    }

    @Override
    @Operation(
        summary = "Crear una nueva tarea",
        description = "Crea una tarea asociada al usuario autenticado",
        responses = {
            @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente",
                content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content)
        }
    )
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        com.previred.desafio.dto.TaskRequest serviceRequest = new com.previred.desafio.dto.TaskRequest(
            taskRequest.getTitle(),
            taskRequest.getDescription(),
            taskRequest.getStatusId()
        );
        
        com.previred.desafio.dto.TaskDTO serviceDTO = taskService.createTask(serviceRequest, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToApiDTO(serviceDTO));
    }

    @Override
    @Operation(
        summary = "Actualizar una tarea",
        description = "Actualiza el título, descripción o estado de una tarea existente",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tarea actualizada exitosamente",
                content = @Content(schema = @Schema(implementation = TaskDTO.class))),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
        @Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id, 
        @Valid @RequestBody TaskRequest taskRequest
    ) {
        com.previred.desafio.dto.TaskRequest serviceRequest = new com.previred.desafio.dto.TaskRequest(
            taskRequest.getTitle(),
            taskRequest.getDescription(),
            taskRequest.getStatusId()
        );
        
        com.previred.desafio.dto.TaskDTO serviceDTO = taskService.updateTask(id, serviceRequest);
        return ResponseEntity.ok(mapToApiDTO(serviceDTO));
    }

    @Override
    @Operation(
        summary = "Eliminar una tarea",
        description = "Elimina físicamente una tarea del sistema por su ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada", content = @Content)
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID de la tarea a eliminar") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    private TaskDTO mapToApiDTO(com.previred.desafio.dto.TaskDTO serviceDTO) {
        TaskDTO apiDTO = new TaskDTO();
        apiDTO.setId(serviceDTO.id());
        apiDTO.setTitle(serviceDTO.title());
        apiDTO.setDescription(serviceDTO.description());
        apiDTO.setStatusName(serviceDTO.statusName());
        apiDTO.setUsername(serviceDTO.username());
        apiDTO.setCreatedAt(serviceDTO.createdAt().atOffset(ZoneOffset.UTC));
        apiDTO.setUpdatedAt(serviceDTO.updatedAt().atOffset(ZoneOffset.UTC));
        return apiDTO;
    }
}
