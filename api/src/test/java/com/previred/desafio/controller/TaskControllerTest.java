package com.previred.desafio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.previred.desafio.dto.TaskDTO;
import com.previred.desafio.dto.TaskRequest;
import com.previred.desafio.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    @WithMockUser(username = "admin")
    void getAllTasks_ShouldReturnPage() throws Exception {
        TaskDTO dto = new TaskDTO(1L, "revisar documentación API", "describir los endpoints en swagger", "PENDIENTE", "admin", LocalDateTime.now(), LocalDateTime.now());
        when(taskService.getAllTasks(any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(dto)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("revisar documentación API"));
    }

    @Test
    @WithMockUser(username = "admin")
    void createTask_ShouldReturnDTO() throws Exception {
        TaskRequest request = new TaskRequest("corregir bug en filtro de estados", "revisar el query del repositorio", 1L);
        TaskDTO dto = new TaskDTO(1L, "corregir bug en filtro de estados", "revisar el query del repositorio", "PENDIENTE", "admin", LocalDateTime.now(), LocalDateTime.now());
        when(taskService.createTask(any(TaskRequest.class), eq("admin"))).thenReturn(dto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("corregir bug en filtro de estados"));
    }

    @Test
    @WithMockUser(username = "admin")
    void updateTask_ShouldReturnDTO() throws Exception {
        TaskRequest request = new TaskRequest("actualizar estados de tareas vencidas", "revisar cron job", 1L);
        TaskDTO dto = new TaskDTO(1L, "actualizar estados de tareas vencidas", "revisar cron job", "PENDIENTE", "admin", LocalDateTime.now(), LocalDateTime.now());
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("actualizar estados de tareas vencidas"));
    }

    @Test
    @WithMockUser(username = "admin")
    void deleteTask_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin")
    void getTasksByStatus_ShouldReturnPage() throws Exception {
        TaskDTO dto = new TaskDTO(1L, "revisar documentación API", "describir endpoints", "PENDIENTE", "admin", LocalDateTime.now(), LocalDateTime.now());
        when(taskService.getAllTasks(eq("PENDIENTE"), any())).thenReturn(new PageImpl<>(Collections.singletonList(dto)));

        mockMvc.perform(get("/api/tasks").param("status", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].statusName").value("PENDIENTE"));
    }

    @Test
    void getAllTasks_ShouldReturnForbidden_WhenNoUser() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden());
    }
}
