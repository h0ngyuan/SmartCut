package com.smartcut.service;

import com.smartcut.model.dto.EngineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EngineClientTest {

    private EngineClient client;

    @BeforeEach
    void setUp() {
        client = new EngineClient("http://localhost:19527");
    }

    @Test
    void healthCheckWhenEngineNotRunningReturnsFalse() {
        boolean result = client.healthCheck();
        assertFalse(result);
    }

    @Test
    void getBaseUrlReturnsConfiguredUrl() {
        assertEquals("http://localhost:19527", client.getBaseUrl());
    }

    @Test
    void buildRequestCreatesValidObject() {
        EngineRequest request = new EngineRequest("cut", "/path/to/video.mp4", "cut highlights");
        assertNotNull(request);
        assertEquals("cut", request.getAction());
        assertEquals("/path/to/video.mp4", request.getSourcePath());
        assertEquals("cut highlights", request.getPrompt());
    }
}
