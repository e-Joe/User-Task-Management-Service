package com.developer.test.service;

import com.developer.test.model.Task;
import com.developer.test.model.User;
import com.developer.test.repository.TaskRepository;
import com.developer.test.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with sample data on first run.
 * Skips seeding if data already exists (persistence).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public DataSeeder(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already has data, skipping seed");
            return;
        }

        logger.info("Seeding database with sample data...");

        User john = userRepository.save(new User("John Doe", "john@example.com", "developer"));
        User jane = userRepository.save(new User("Jane Smith", "jane@example.com", "designer"));
        User bob = userRepository.save(new User("Bob Johnson", "bob@example.com", "manager"));

        taskRepository.save(new Task("Implement authentication", "pending", john.getId()));
        taskRepository.save(new Task("Design user interface", "in-progress", jane.getId()));
        taskRepository.save(new Task("Review code changes", "completed", bob.getId()));

        logger.info("Seeded {} users and {} tasks", userRepository.count(), taskRepository.count());
    }
}
