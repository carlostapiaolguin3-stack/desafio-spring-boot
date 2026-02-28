package com.previred.desafio.repository;

import com.previred.desafio.model.Task;
import com.previred.desafio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    Page<Task> findByStatus_Name(String statusName, Pageable pageable);
}
