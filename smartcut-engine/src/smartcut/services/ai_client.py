"""AI vendor API client — unified interface for multiple AI providers."""

import logging

logger = logging.getLogger(__name__)


class AIClient:
    """Unified client for AI vendor APIs (vision, NLP, etc.)."""

    def __init__(self, api_key: str = "", base_url: str = ""):
        self.api_key = api_key
        self.base_url = base_url

    def analyze_video(self, video_path: str, prompt: str) -> str:
        """Stub — send video + prompt to AI for analysis.

        Args:
            video_path: Path to the video file.
            prompt: Natural language instruction.

        Returns:
            AI response text.
        """
        logger.info("AI analyze: video=%s prompt=%s", video_path, prompt)
        raise NotImplementedError("AIClient.analyze_video not yet implemented")
