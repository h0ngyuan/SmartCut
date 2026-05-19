package com.smartcut.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class EngineRequest {

    private final String action;
    private final String sourcePath;
    private final String prompt;

    public EngineRequest(
            @JsonProperty("action") String action,
            @JsonProperty("sourcePath") String sourcePath,
            @JsonProperty("prompt") String prompt) {
        this.action = action;
        this.sourcePath = sourcePath;
        this.prompt = prompt;
    }
}
