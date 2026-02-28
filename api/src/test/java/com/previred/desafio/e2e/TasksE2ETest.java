package com.previred.desafio.e2e;

import com.previred.desafio.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class TasksE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private static String jwtToken;
    private static Long createdTaskId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
    }

    @Test
    @Order(1)
    void flujo_autenticacion_exitosa() {
        AuthRequest authRequest = new AuthRequest("admin", "admin123");
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(baseUrl + "/auth/login", authRequest, AuthResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
        jwtToken = response.getBody().token();
    }

    @Test
    @Order(2)
    void flujo_creacion_tarea_exitosa() {
        TaskRequest taskRequest = new TaskRequest("revisar integración con H2", "verificar que el schema se crea correctamente al arrancar", 1L);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<TaskRequest> request = new HttpEntity<>(taskRequest, headers);

        ResponseEntity<TaskDTO> response = restTemplate.postForEntity(baseUrl + "/tasks", request, TaskDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("revisar integración con H2", response.getBody().title());
        createdTaskId = response.getBody().id();
    }

    @Test
    @Order(3)
    void flujo_listado_tareas_paginado() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(baseUrl + "/tasks", HttpMethod.GET, request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("content"));
        List<?> tasks = (List<?>) response.getBody().get("content");
        assertTrue(tasks.size() > 0);
    }

    @Test
    @Order(4)
    void flujo_listado_tareas_por_estado() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(baseUrl + "/tasks?status=PENDIENTE", HttpMethod.GET, request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<?> tasks = (List<?>) response.getBody().get("content");
        assertNotNull(tasks);
        // Al menos la tarea creada en el test anterior debería estar aquí
        assertTrue(tasks.size() > 0);
    }

    @Test
    @Order(5)
    void flujo_actualizacion_tarea_exitosa() {
        TaskRequest updateRequest = new TaskRequest("revisar integración con H2 — actualizada", "se encontró un bug en el schema", 2L);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<TaskRequest> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<TaskDTO> response = restTemplate.exchange(baseUrl + "/tasks/" + createdTaskId, HttpMethod.PUT, request, TaskDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("revisar integración con H2 — actualizada", response.getBody().title());
    }

    @Test
    @Order(6)
    void flujo_eliminacion_tarea_exitosa() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/tasks/" + createdTaskId, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(7)
    void flujo_error_acceso_sin_token() {
        ResponseEntity<Void> response = restTemplate.getForEntity(baseUrl + "/tasks", Void.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @Order(8)
    void flujo_error_login_credenciales_invalidas() {
        AuthRequest authRequest = new AuthRequest("admin", "wrongpassword");
        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/auth/login", authRequest, Map.class);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(9)
    void flujo_error_recurso_no_encontrado() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Intento borrar algo que no existe
        ResponseEntity<Map> response = restTemplate.exchange(baseUrl + "/tasks/9999", HttpMethod.DELETE, request, Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("error").toString().contains("no encontrado"));
    }
}
