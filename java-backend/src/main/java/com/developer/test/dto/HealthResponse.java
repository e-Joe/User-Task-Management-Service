package com.developer.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("uptime")
    private String uptime;

    @JsonProperty("version")
    private String version;

    @JsonProperty("data")
    private DataInfo data;

    @JsonProperty("memory")
    private MemoryInfo memory;

    public HealthResponse() {
    }

    public HealthResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public DataInfo getData() {
        return data;
    }

    public void setData(DataInfo data) {
        this.data = data;
    }

    public MemoryInfo getMemory() {
        return memory;
    }

    public void setMemory(MemoryInfo memory) {
        this.memory = memory;
    }

    public static class DataInfo {
        @JsonProperty("users")
        private int users;

        @JsonProperty("tasks")
        private int tasks;

        public DataInfo(int users, int tasks) {
            this.users = users;
            this.tasks = tasks;
        }

        public int getUsers() { return users; }
        public int getTasks() { return tasks; }
    }

    public static class MemoryInfo {
        @JsonProperty("used")
        private String used;

        @JsonProperty("free")
        private String free;

        @JsonProperty("max")
        private String max;

        public MemoryInfo(String used, String free, String max) {
            this.used = used;
            this.free = free;
            this.max = max;
        }

        public String getUsed() { return used; }
        public String getFree() { return free; }
        public String getMax() { return max; }
    }
}
