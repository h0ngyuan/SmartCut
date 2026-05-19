package com.smartcut.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class EngineResponse {

    public enum Status { SUCCESS, ERROR }

    private final Status status;
    private final String resultPath;
    private final String message;

    @JsonCreator
    public EngineResponse(
            @JsonProperty("status") Status status,
            @JsonProperty("resultPath") String resultPath,
            @JsonProperty("message") String message) {
        this.status = status;
        this.resultPath = resultPath;
        this.message = message;
    }

    public static EngineResponse success(String path) {
        return new EngineResponse(Status.SUCCESS, path, null);
    }

    public static EngineResponse error(String message) {
        return new EngineResponse(Status.ERROR, null, message);
    }
}
