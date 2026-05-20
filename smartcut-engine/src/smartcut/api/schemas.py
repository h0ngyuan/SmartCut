"""Pydantic models for API request/response validation."""

from pydantic import BaseModel, Field


class TaskRequest(BaseModel):
    """A video editing task submitted by the GUI."""

    action: str = Field(description="Action type: cut, highlight, subtitle, speed")
    source_path: str = Field(description="Absolute path to the source video")
    prompt: str = Field(description="Natural language editing instruction")


class TaskResponse(BaseModel):
    """Result returned to the GUI after processing."""

    status: str = Field(default="SUCCESS", description="SUCCESS or ERROR")
    result_path: str | None = Field(default=None, description="Path to processed video")
    message: str | None = Field(default=None, description="Error message if status is ERROR")


class HealthResponse(BaseModel):
    """Health check response."""

    status: str = "ok"
    version: str = "0.1.0"
