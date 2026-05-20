"""Speech-to-text transcription and subtitle generation via Whisper."""

import logging

logger = logging.getLogger(__name__)


class Transcriber:
    """Transcribes audio to text and generates subtitle files."""

    def transcribe(self, video_path: str) -> str:
        """Stub — transcribe audio to text.

        Args:
            video_path: Path to the video file.

        Returns:
            Transcribed text content.
        """
        logger.info("Transcribing video: %s", video_path)
        raise NotImplementedError("Transcriber.transcribe not yet implemented")

    def generate_subtitles(self, video_path: str, output_srt: str) -> None:
        """Stub — generate SRT subtitle file.

        Args:
            video_path: Path to the video file.
            output_srt: Path for the output .srt file.
        """
        logger.info("Generating subtitles: %s -> %s", video_path, output_srt)
        raise NotImplementedError("Transcriber.generate_subtitles not yet implemented")
