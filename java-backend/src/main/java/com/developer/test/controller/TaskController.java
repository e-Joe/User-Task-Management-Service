package com.developer.test.controller;

import com.developer.test.dto.CreateTaskRequest;
import com.developer.test.dto.TasksResponse;
import com.developer.test.dto.UpdateTaskRequest;
import com.developer.test.model.Task;
import com.developer.test.service.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final DataStore dataStore;
    
    public TaskController(DataStore dataStore) {
        this.dataStore = dataStore;
    }
    
    @GetMapping
    public ResponseEntity<TasksResponse> getTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId) {
        List<com.developer.test.model.Task> tasks = dataStore.getTasks(status, userId);
        TasksResponse response = new TasksResponse(tasks, tasks.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new task.
     *
     * @param request the task creation request containing title, status, and userId
     * @return the created task with status 201
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = dataStore.createTask(request.getTitle(), request.getStatus(), request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    /**
     * Updates an existing task. Supports partial updates — only provided fields are changed.
     *
     * @param id      the task ID
     * @param request the update request with optional title, status, and userId
     * @return the updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody UpdateTaskRequest request) {
        Task task = dataStore.updateTask(id, request.getTitle(), request.getStatus(), request.getUserId());
        return ResponseEntity.ok(task);
    }
}
