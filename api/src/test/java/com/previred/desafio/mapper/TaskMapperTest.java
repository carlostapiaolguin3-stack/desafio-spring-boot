package com.previred.desafio.mapper;

import com.previred.desafio.dto.TaskDTO;
import com.previred.desafio.model.Task;
import com.previred.desafio.model.TaskStatus;
import com.previred.desafio.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TaskMapperImpl.class})
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void toDTO_ShouldMapAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().id(1L).username("testuser").build();
        TaskStatus status = TaskStatus.builder().id(1L).name("PENDIENTE").build();
        Task task = Task.builder()
                .id(42L)
                .title("Mi tarea")
                .description("Descripcion de la tarea")
                .status(status)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();

        TaskDTO dto = taskMapper.toDTO(task);

        assertNotNull(dto);
        assertEquals(42L, dto.id());
        assertEquals("Mi tarea", dto.title());
        assertEquals("Descripcion de la tarea", dto.description());
        assertEquals("PENDIENTE", dto.statusName());
        assertEquals("testuser", dto.username());
        assertEquals(now, dto.createdAt());
        assertEquals(now, dto.updatedAt());
    }
}
