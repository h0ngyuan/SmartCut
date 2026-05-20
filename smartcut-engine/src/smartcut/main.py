"""FastAPI application entry point for the SmartCut engine."""

from fastapi import FastAPI

from .api.router import router

app = FastAPI(
    title="SmartCut Engine",
    version="0.1.0",
    description="Video processing engine with AI capabilities",
)

app.include_router(router)
