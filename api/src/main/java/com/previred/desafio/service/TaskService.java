package com.previred.desafio.service;

import com.previred.desafio.dto.TaskDTO;
import com.previred.desafio.dto.TaskRequest;
import com.previred.desafio.exception.ResourceNotFoundException;
import com.previred.desafio.mapper.TaskMapper;
import com.previred.desafio.model.Task;
import com.previred.desafio.model.TaskStatus;
import com.previred.desafio.model.User;
import com.previred.desafio.repository.TaskRepository;
import com.previred.desafio.repository.TaskStatusRepository;
import com.previred.desafio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public Page<TaskDTO> getAllTasks(String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            if (!taskStatusRepository.existsByName(status)) {
                log.warn("Intento de filtrado por estado inexistente: {}", status);
                throw new ResourceNotFoundException("El estado '" + status + "' no es válido.");
            }
            log.info("Listando tareas filtradas por estado: {}", status);
            return taskRepository.findByStatus_Name(status, pageable).map(taskMapper::toDTO);
        }
        log.info("Listando todas las tareas (paginado)");
        return taskRepository.findAll(pageable).map(taskMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        return taskRepository.findByUser(user).stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @Transactional
    public TaskDTO createTask(TaskRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        TaskStatus status = taskStatusRepository.findById(request.statusId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado", request.statusId()));

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(status)
                .user(user)
                .build();

        log.info("Creando nueva tarea: '{}' para el usuario: '{}'", request.title(), username);
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea", id));

        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.statusId() != null) {
            TaskStatus status = taskStatusRepository.findById(request.statusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Estado", request.statusId()));
            task.setStatus(status);
        }

        log.info("Tarea ID {} actualizada con éxito", id);
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarea", id);
        }
        taskRepository.deleteById(id);
        log.info("Tarea ID {} eliminada físicamente", id);
    }
}
