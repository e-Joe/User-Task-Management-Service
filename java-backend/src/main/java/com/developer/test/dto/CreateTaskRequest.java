package com.developer.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "Status is required")
    @JsonProperty("status")
    private String status;

    @NotNull(message = "UserId is required")
    @JsonProperty("userId")
    private Integer userId;

    public CreateTaskRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
