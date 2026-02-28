package com.previred.desafio.exception;

import com.previred.desafio.controller.TaskController;
import com.previred.desafio.service.TaskService;
import com.previred.desafio.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void handleResourceNotFoundException_ShouldReturn404() throws Exception {
        when(taskService.getAllTasks(any(), any())).thenThrow(new ResourceNotFoundException("Tarea", 1L));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Tarea con ID 1 no encontrado"));
    }

    @Test
    @WithMockUser
    void handleUnauthorizedAccessException_ShouldReturn403() throws Exception {
        when(taskService.getAllTasks(any(), any())).thenThrow(new UnauthorizedAccessException("Acceso denegado"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Acceso denegado"));
    }

    @Test
    @WithMockUser
    void handleGeneralException_ShouldReturn500() throws Exception {
        when(taskService.getAllTasks(any(), any())).thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("ocurrió un error inesperado"));
    }
}
