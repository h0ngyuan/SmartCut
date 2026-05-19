package com.smartcut.model;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class VideoTask {

    public enum Status { PENDING, PROCESSING, DONE, FAILED }

    private final UUID id;
    private final Path sourceVideo;
    private final String userPrompt;
    private Status status;
    private Path resultVideo;
    private String errorMessage;
    private final LocalDateTime createdAt;

    public VideoTask(Path sourceVideo, String userPrompt) {
        this.id = UUID.randomUUID();
        this.sourceVideo = sourceVideo;
        this.userPrompt = userPrompt;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    @JsonCreator
    public VideoTask(
            @JsonProperty("id") UUID id,
            @JsonProperty("sourceVideo") Path sourceVideo,
            @JsonProperty("userPrompt") String userPrompt,
            @JsonProperty("status") Status status,
            @JsonProperty("resultVideo") Path resultVideo,
            @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.sourceVideo = sourceVideo;
        this.userPrompt = userPrompt;
        this.status = status;
        this.resultVideo = resultVideo;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    public void markProcessing() { this.status = Status.PROCESSING; }
    public void markDone(Path result) { this.status = Status.DONE; this.resultVideo = result; }
    public void markFailed(String error) { this.status = Status.FAILED; this.errorMessage = error; }
}
