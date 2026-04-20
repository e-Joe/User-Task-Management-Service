package com.developer.test.service;

import com.developer.test.exception.ResourceNotFoundException;
import com.developer.test.exception.ValidationException;
import com.developer.test.model.Task;
import com.developer.test.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataStoreTest {

    private DataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new DataStore();
    }

    // --- User creation tests ---

    @Test
    void createUser_shouldReturnUserWithGeneratedId() {
        User user = dataStore.createUser("Test User", "test@example.com", "developer");

        assertEquals(4, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("developer", user.getRole());
    }

    @Test
    void createUser_shouldBeRetrievableAfterCreation() {
        User created = dataStore.createUser("New User", "new@example.com", "tester");
        User fetched = dataStore.getUserById(created.getId());

        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals("New User", fetched.getName());
    }

    @Test
    void createUser_shouldIncrementIds() {
        User first = dataStore.createUser("First", "first@test.com", "dev");
        User second = dataStore.createUser("Second", "second@test.com", "dev");

        assertEquals(first.getId() + 1, second.getId());
    }

    // --- Task creation tests ---

    @Test
    void createTask_shouldReturnTaskWithGeneratedId() {
        Task task = dataStore.createTask("New Task", "pending", 1);

        assertEquals(4, task.getId());
        assertEquals("New Task", task.getTitle());
        assertEquals("pending", task.getStatus());
        assertEquals(1, task.getUserId());
    }

    @Test
    void createTask_shouldRejectInvalidStatus() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> dataStore.createTask("Task", "invalid", 1));

        assertTrue(ex.getMessage().contains("Invalid status"));
    }

    @Test
    void createTask_shouldRejectNonExistentUser() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> dataStore.createTask("Task", "pending", 999));

        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void createTask_shouldAcceptAllValidStatuses() {
        assertDoesNotThrow(() -> dataStore.createTask("T1", "pending", 1));
        assertDoesNotThrow(() -> dataStore.createTask("T2", "in-progress", 1));
        assertDoesNotThrow(() -> dataStore.createTask("T3", "completed", 1));
    }

    // --- Task update tests ---

    @Test
    void updateTask_shouldUpdateOnlyProvidedFields() {
        Task updated = dataStore.updateTask(1, null, "completed", null);

        assertEquals("Implement authentication", updated.getTitle());
        assertEquals("completed", updated.getStatus());
        assertEquals(1, updated.getUserId());
    }

    @Test
    void updateTask_shouldUpdateTitle() {
        Task updated = dataStore.updateTask(1, "Updated Title", null, null);

        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    void updateTask_shouldUpdateUserId() {
        Task updated = dataStore.updateTask(1, null, null, 2);

        assertEquals(2, updated.getUserId());
    }

    @Test
    void updateTask_shouldThrowForNonExistentTask() {
        assertThrows(ResourceNotFoundException.class,
                () -> dataStore.updateTask(999, "Title", null, null));
    }

    @Test
    void updateTask_shouldRejectInvalidStatus() {
        assertThrows(ValidationException.class,
                () -> dataStore.updateTask(1, null, "invalid", null));
    }

    @Test
    void updateTask_shouldRejectNonExistentUserId() {
        assertThrows(ValidationException.class,
                () -> dataStore.updateTask(1, null, null, 999));
    }

    // --- Existing data tests ---

    @Test
    void shouldHaveInitialSampleData() {
        assertEquals(3, dataStore.getUsers().size());
        assertTrue(dataStore.userExists(1));
        assertTrue(dataStore.userExists(2));
        assertTrue(dataStore.userExists(3));
        assertFalse(dataStore.userExists(99));
    }
}
