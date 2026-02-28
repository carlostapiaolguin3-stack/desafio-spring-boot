package com.previred.desafio.repository;

import com.previred.desafio.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    Optional<TaskStatus> findByName(String name);
    boolean existsByName(String name);
}
