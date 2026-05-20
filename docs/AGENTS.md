# Agent Responsibilities

## Java GUI Agent

- **Responsible for:** SmartCut desktop GUI development
- **Tech stack:** JDK 21, JavaFX, Maven, Jackson
- **File path:** `smartcut-gui/src/**`
- **Scope:**
  - UI views and user interaction (JavaFX)
  - Scheduling the Python engine via EngineClient
  - Project management (Project/VideoTask)
  - Video preview and export UI
- **Must not:**
  - Call FFmpeg or Python processes directly (must use EngineClient)
  - Write video processing or AI analysis logic in the UI layer
  - Modify Python engine code

## Python Engine Agent

- **Responsible for:** Video processing engine
- **Tech stack:** Python 3.12, FastAPI, MoviePy, FFmpeg, Whisper
- **File path:** `smartcut-engine/src/**`
- **Scope:**
  - Video editing and compositing (MoviePy + FFmpeg)
  - AI API calls (vision analysis, NLP)
  - Speech recognition and subtitle generation (Whisper)
  - FastAPI routing and request validation
- **Must not:**
  - Modify Java GUI code
  - Handle GUI state or logic
  - Directly manipulate the user's file system (paths must come through the API)

## Cross-Layer Rules

- Java ↔ Python communication only via `localhost:19527` HTTP + JSON
- JSON schema changes must be synced on both sides (`EngineRequest/EngineResponse` ↔ `TaskRequest/TaskResponse`)
- Tests on each side must be independently runnable (no dependency on the other side being started)
