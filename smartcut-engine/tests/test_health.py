"""Tests for the health endpoint and API schemas."""

import pydantic
import pytest
from fastapi.testclient import TestClient

from src.smartcut.api.schemas import HealthResponse, TaskRequest, TaskResponse
from src.smartcut.main import app


class TestHealthEndpoint:
    """Health check endpoint tests."""

    @pytest.fixture
    def client(self):
        return TestClient(app)

    def test_health_returns_ok(self, client):
        response = client.get("/api/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "ok"
        assert data["version"] == "0.1.0"

    def test_task_endpoint_returns_stub_error(self, client):
        response = client.post("/api/task", json={
            "action": "cut",
            "source_path": "/tmp/test.mp4",
            "prompt": "create highlights"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "ERROR"


class TestSchemas:
    """Pydantic schema validation tests."""

    def test_health_response_defaults(self):
        hr = HealthResponse()
        assert hr.status == "ok"
        assert hr.version == "0.1.0"

    def test_task_request_valid(self):
        tr = TaskRequest(action="cut", source_path="/v/test.mp4", prompt="edit")
        assert tr.action == "cut"

    def test_task_request_missing_field(self):
        with pytest.raises(pydantic.ValidationError):
            TaskRequest(action="cut")  # missing source_path and prompt

    def test_task_response_success(self):
        tr = TaskResponse(status="SUCCESS", result_path="/out.mp4")
        assert tr.status == "SUCCESS"
        assert tr.result_path == "/out.mp4"

    def test_task_response_error(self):
        tr = TaskResponse(status="ERROR", message="Something went wrong")
        assert tr.status == "ERROR"
        assert tr.message == "Something went wrong"
