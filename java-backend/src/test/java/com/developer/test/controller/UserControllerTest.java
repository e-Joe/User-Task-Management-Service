package com.developer.test.controller;

import com.developer.test.model.User;
import com.developer.test.service.DataStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataStore dataStore;

    @Test
    void getUsers_shouldReturnUsersList() throws Exception {
        when(dataStore.getUsers()).thenReturn(List.of(
                createUser(1, "John", "john@test.com", "dev")
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.users[0].name").value("John"));
    }

    @Test
    void createUser_withValidData_shouldReturn201() throws Exception {
        when(dataStore.createUser(anyString(), anyString(), anyString()))
                .thenReturn(createUser(1, "Ilija", "ilija@test.com", "developer"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ilija\",\"email\":\"ilija@test.com\",\"role\":\"developer\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ilija"))
                .andExpect(jsonPath("$.email").value("ilija@test.com"));
    }

    @Test
    void createUser_withMissingName_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@test.com\",\"role\":\"dev\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUser_withInvalidEmail_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"email\":\"notanemail\",\"role\":\"dev\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUser_withEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    private User createUser(int id, String name, String email, String role) {
        User user = new User(name, email, role);
        user.setId(id);
        return user;
    }
}
