"""Tests for service stubs — verify they raise NotImplementedError."""

import pytest

from src.smartcut.services.ai_client import AIClient
from src.smartcut.services.analyzer import VideoAnalyzer
from src.smartcut.services.editor import VideoEditor
from src.smartcut.services.transcriber import Transcriber


class TestAIClient:
    """AIClient stub tests."""

    def test_analyze_video_raises(self):
        client = AIClient(api_key="test", base_url="http://test")
        with pytest.raises(NotImplementedError, match="AIClient.analyze_video"):
            client.analyze_video("/tmp/test.mp4", "analyze this")


class TestVideoAnalyzer:
    """VideoAnalyzer stub tests."""

    def test_find_highlights_raises(self):
        analyzer = VideoAnalyzer()
        with pytest.raises(NotImplementedError, match="VideoAnalyzer.find_highlights"):
            analyzer.find_highlights("/tmp/test.mp4", "find highlights")


class TestVideoEditor:
    """VideoEditor stub tests."""

    def test_process_raises(self):
        editor = VideoEditor()
        with pytest.raises(NotImplementedError, match="VideoEditor.process"):
            editor.process("/tmp/test.mp4", "cut", "edit this")


class TestTranscriber:
    """Transcriber stub tests."""

    def test_transcribe_raises(self):
        transcriber = Transcriber()
        with pytest.raises(NotImplementedError, match="Transcriber.transcribe"):
            transcriber.transcribe("/tmp/test.mp4")

    def test_generate_subtitles_raises(self):
        transcriber = Transcriber()
        with pytest.raises(NotImplementedError, match="Transcriber.generate_subtitles"):
            transcriber.generate_subtitles("/tmp/test.mp4", "/tmp/test.srt")
