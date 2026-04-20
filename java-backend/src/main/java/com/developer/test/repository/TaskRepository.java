package com.developer.test.repository;

import com.developer.test.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByStatus(String status);
    List<Task> findByUserId(int userId);
    List<Task> findByStatusAndUserId(String status, int userId);
}
