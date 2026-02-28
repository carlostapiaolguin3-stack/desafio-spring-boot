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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskStatusRepository taskStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private TaskStatus status;
    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("carlos.dev")
                .build();

        status = TaskStatus.builder()
                .id(1L)
                .name("PENDIENTE")
                .build();

        LocalDateTime now = LocalDateTime.now();
        task = Task.builder()
                .id(1L)
                .title("revisar documentación de la API")
                .description("documentar los endpoints en swagger")
                .user(user)
                .status(status)
                .createdAt(now)
                .updatedAt(now)
                .build();

        taskDTO = new TaskDTO(1L, "revisar documentación de la API", "documentar los endpoints en swagger", "PENDIENTE", "carlos.dev", now, now);
        // lenient: este stub no se usa en tests que lanzan excepcion antes de llegar al mapper
        lenient().when(taskMapper.toDTO(task)).thenReturn(taskDTO);
    }

    @Test
    void getAllTasks_ShouldReturnPage_WithoutStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getAllTasks(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("revisar documentación de la API", result.getContent().get(0).title());
    }

    @Test
    void getAllTasks_ShouldReturnPage_WithStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
        when(taskStatusRepository.existsByName("PENDIENTE")).thenReturn(true);
        when(taskRepository.findByStatus_Name(eq("PENDIENTE"), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskDTO> result = taskService.getAllTasks("PENDIENTE", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("PENDIENTE", result.getContent().get(0).statusName());
    }

    @Test
    void getAllTasks_ShouldThrowException_WhenStatusInvalid() {
        Pageable pageable = PageRequest.of(0, 10);
        when(taskStatusRepository.existsByName("INVALID")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.getAllTasks("INVALID", pageable));
    }

    @Test
    void getTasksByUser_ShouldReturnList() {
        when(userRepository.findByUsername("carlos.dev")).thenReturn(Optional.of(user));
        when(taskRepository.findByUser(user)).thenReturn(Collections.singletonList(task));

        List<TaskDTO> result = taskService.getTasksByUser("carlos.dev");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getTasksByUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTasksByUser("nobody"));
    }

    @Test
    void createTask_ShouldReturnDTO() {
        TaskRequest request = new TaskRequest("agregar validación de fechas", "revisar que la fecha de inicio no sea futura", 1L);
        when(userRepository.findByUsername("carlos.dev")).thenReturn(Optional.of(user));
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO result = taskService.createTask(request, "carlos.dev");

        assertNotNull(result);
        assertEquals("revisar documentación de la API", result.title()); // el mock devuelve taskDTO
    }

    @Test
    void createTask_ShouldThrowException_WhenStatusNotFound() {
        TaskRequest request = new TaskRequest("tarea cualquiera", "desc", 99L);
        when(userRepository.findByUsername("carlos.dev")).thenReturn(Optional.of(user));
        when(taskStatusRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request, "carlos.dev"));
    }

    @Test
    void updateTask_ShouldReturnDTO() {
        TaskRequest request = new TaskRequest("Updated", "Desc", 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO result = taskService.updateTask(1L, request);

        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldOnlyUpdateProvidedFields() {
        TaskRequest request = new TaskRequest(null, null, null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO result = taskService.updateTask(1L, request);

        assertNotNull(result);
        assertEquals("revisar documentación de la API", result.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        TaskRequest request = new TaskRequest("Updated", "Desc", 1L);
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(99L, request));
    }

    @Test
    void updateTask_ShouldThrowException_WhenStatusNotFound() {
        TaskRequest request = new TaskRequest("Updated", "Desc", 99L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskStatusRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(1L, request));
    }

    @Test
    void deleteTask_ShouldDelete_WhenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenNotExists() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(99L));
    }
}
