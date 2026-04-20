package com.developer.test.controller;

import com.developer.test.dto.HealthResponse;
import com.developer.test.service.DataStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class HealthController {

    private final DataStore dataStore;

    public HealthController(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = new HealthResponse("ok", "Java backend is running");

        // Uptime
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        response.setUptime(formatUptime(uptimeMs));

        // Version
        response.setVersion("1.0.0");

        // Data counts
        response.setData(new HealthResponse.DataInfo(
                dataStore.getUsers().size(),
                dataStore.getTasks(null, null).size()
        ));

        // Memory
        Runtime runtime = Runtime.getRuntime();
        long usedMem = runtime.totalMemory() - runtime.freeMemory();
        response.setMemory(new HealthResponse.MemoryInfo(
                formatBytes(usedMem),
                formatBytes(runtime.freeMemory()),
                formatBytes(runtime.maxMemory())
        ));

        return ResponseEntity.ok(response);
    }

    private String formatUptime(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%dh %dm %ds", hours, minutes, secs);
    }

    private String formatBytes(long bytes) {
        return String.format("%dMB", bytes / (1024 * 1024));
    }
}
