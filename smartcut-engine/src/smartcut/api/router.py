"""API route definitions."""

from fastapi import APIRouter

from .schemas import HealthResponse, TaskRequest, TaskResponse

router = APIRouter(prefix="/api")


@router.get("/health", response_model=HealthResponse)
async def health():
    """Health check endpoint."""
    return HealthResponse()


@router.post("/task", response_model=TaskResponse)
async def submit_task(request: TaskRequest):
    """Submit a video editing task.

    This is a stub — business logic will be added in the services layer.
    """
    return TaskResponse(
        status="ERROR",
        message=f"Action '{request.action}' not yet implemented",
    )
