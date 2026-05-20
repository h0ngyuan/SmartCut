"""Video editing orchestration — cut, merge, speed, effects via MoviePy/FFmpeg."""

import logging

logger = logging.getLogger(__name__)


class VideoEditor:
    """Orchestrates video editing operations."""

    def process(self, source_path: str, action: str, prompt: str) -> str:
        """Stub — implement later.

        Args:
            source_path: Absolute path to the input video.
            action: Operation type (cut, highlight, subtitle, speed).
            prompt: Natural language instruction.

        Returns:
            Path to the processed video file.
        """
        logger.info("Processing video: source=%s action=%s", source_path, action)
        raise NotImplementedError("VideoEditor.process not yet implemented")
