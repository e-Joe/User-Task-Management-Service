package com.developer.test.service;

import com.developer.test.exception.ResourceNotFoundException;
import com.developer.test.exception.ValidationException;
import com.developer.test.model.Task;
import com.developer.test.model.User;
import com.developer.test.dto.StatsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory data store for users and tasks.
 * Uses ConcurrentHashMap and AtomicInteger for thread-safe concurrent access.
 */
@Service
public class DataStore {

    private static final Set<String> VALID_STATUSES = Set.of("pending", "in-progress", "completed");

    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger nextUserId = new AtomicInteger(1);
    private final AtomicInteger nextTaskId = new AtomicInteger(1);

    public DataStore() {
        // Initialize with sample data
        users.put(1, new User(1, "John Doe", "john@example.com", "developer"));
        users.put(2, new User(2, "Jane Smith", "jane@example.com", "designer"));
        users.put(3, new User(3, "Bob Johnson", "bob@example.com", "manager"));
        
        tasks.put(1, new Task(1, "Implement authentication", "pending", 1));
        tasks.put(2, new Task(2, "Design user interface", "in-progress", 2));
        tasks.put(3, new Task(3, "Review code changes", "completed", 3));
        
        nextUserId.set(4);
        nextTaskId.set(4);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    public List<Task> getTasks(String status, String userId) {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        
        return allTasks.stream()
                .filter(task -> {
                    boolean matchStatus = status == null || status.isEmpty() || task.getStatus().equals(status);
                    boolean matchUserId = userId == null || userId.isEmpty() || 
                            task.getUserId() == Integer.parseInt(userId);
                    return matchStatus && matchUserId;
                })
                .collect(Collectors.toList());
    }

    public StatsResponse getStats() {
        StatsResponse stats = new StatsResponse();
        stats.getUsers().setTotal(users.size());
        stats.getTasks().setTotal(tasks.size());
        
        for (Task task : tasks.values()) {
            switch (task.getStatus()) {
                case "pending":
                    stats.getTasks().setPending(stats.getTasks().getPending() + 1);
                    break;
                case "in-progress":
                    stats.getTasks().setInProgress(stats.getTasks().getInProgress() + 1);
                    break;
                case "completed":
                    stats.getTasks().setCompleted(stats.getTasks().getCompleted() + 1);
                    break;
            }
        }
        
        return stats;
    }

    /**
     * Creates a new user with an auto-generated ID.
     *
     * @param name  the user's name
     * @param email the user's email
     * @param role  the user's role
     * @return the created user
     */
    public User createUser(String name, String email, String role) {
        int id = nextUserId.getAndIncrement();
        User user = new User(id, name, email, role);
        users.put(id, user);
        return user;
    }

    /**
     * Creates a new task with an auto-generated ID.
     * Validates that the status is valid and the userId references an existing user.
     *
     * @param title  the task title
     * @param status the task status (pending, in-progress, completed)
     * @param userId the ID of the user assigned to this task
     * @return the created task
     * @throws ValidationException if status is invalid or userId does not exist
     */
    public Task createTask(String title, String status, int userId) {
        validateStatus(status);
        if (!userExists(userId)) {
            throw new ValidationException("User with ID " + userId + " does not exist");
        }
        int id = nextTaskId.getAndIncrement();
        Task task = new Task(id, title, status, userId);
        tasks.put(id, task);
        return task;
    }

    /**
     * Updates an existing task. Only non-null fields are updated (partial update).
     *
     * @param id     the task ID
     * @param title  new title (or null to keep existing)
     * @param status new status (or null to keep existing)
     * @param userId new userId (or null to keep existing)
     * @return the updated task
     * @throws ResourceNotFoundException if the task does not exist
     * @throws ValidationException       if status or userId is invalid
     */
    public Task updateTask(int id, String title, String status, Integer userId) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new ResourceNotFoundException("Task with ID " + id + " not found");
        }
        if (status != null) {
            validateStatus(status);
            task.setStatus(status);
        }
        if (userId != null) {
            if (!userExists(userId)) {
                throw new ValidationException("User with ID " + userId + " does not exist");
            }
            task.setUserId(userId);
        }
        if (title != null) {
            task.setTitle(title);
        }
        return task;
    }

    public boolean userExists(int id) {
        return users.containsKey(id);
    }

    private void validateStatus(String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new ValidationException("Invalid status: '" + status + "'. Must be one of: " + VALID_STATUSES);
        }
    }
}
