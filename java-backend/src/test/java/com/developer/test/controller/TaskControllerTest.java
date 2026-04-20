package com.developer.test.controller;

import com.developer.test.exception.ResourceNotFoundException;
import com.developer.test.exception.ValidationException;
import com.developer.test.model.Task;
import com.developer.test.service.DataStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataStore dataStore;

    @Test
    void getTasks_shouldReturnTasksList() throws Exception {
        when(dataStore.getTasks(any(), any())).thenReturn(List.of(
                new Task(1, "Task 1", "pending", 1)
        ));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.tasks[0].title").value("Task 1"));
    }

    @Test
    void createTask_withValidData_shouldReturn201() throws Exception {
        when(dataStore.createTask(anyString(), anyString(), anyInt()))
                .thenReturn(new Task(1, "New Task", "pending", 1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Task\",\"status\":\"pending\",\"userId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    void createTask_withMissingTitle_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"pending\",\"userId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createTask_withInvalidStatus_shouldReturn400() throws Exception {
        when(dataStore.createTask(anyString(), eq("invalid"), anyInt()))
                .thenThrow(new ValidationException("Invalid status"));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Task\",\"status\":\"invalid\",\"userId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateTask_withValidData_shouldReturn200() throws Exception {
        when(dataStore.updateTask(eq(1), isNull(), eq("completed"), isNull()))
                .thenReturn(new Task(1, "Task 1", "completed", 1));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"));
    }

    @Test
    void updateTask_nonExistentTask_shouldReturn404() throws Exception {
        when(dataStore.updateTask(eq(999), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Task with ID 999 not found"));

        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"completed\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
