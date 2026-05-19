# SmartCut Harness 工程基础设施设计

日期：2026-05-19

## 范围

本文档仅覆盖 **Harness 工程基础设施搭建**（编译、代码检查、架构测试、CI/CD、目录结构）。
业务功能设计（视频剪辑、AI 对话等）后续单独出 spec。

## 整体架构

```
┌─────────────────────────────────────────────┐
│                 SmartCut                      │
│                                               │
│  ┌──────────────────┐  HTTP (localhost:19527) ┌──────────────────┐
│  │  JavaFX GUI       │ ────────────────────→ │  Python Engine    │
│  │  (JDK 21)         │ ←──────────────────── │  (Python 3.12)    │
│  │                   │    REST + JSON        │                   │
│  │  视频导入/预览     │                       │  FFmpeg 剪辑      │
│  │  对话式交互界面     │                       │  AI API 调用      │
│  │  项目管理          │                       │  语音识别/字幕     │
│  │  结果预览/导出     │                       │  视频分析         │
│  └──────────────────┘                       └──────────────────┘
```

- **通信**：Python 起 FastAPI 服务在 `localhost:19527`，Java 通过 `java.net.http.HttpClient` 调用。JSON 交换数据。
- **启动**：JavaFX 启动时 spawn Python 子进程，窗口关闭时杀掉子进程。
- **将来扩展**：如果切 Web 版，Python 引擎可直接暴露为公开 API。

## Java 侧（smartcut-gui）

### 技术选型

| 项目 | 选型 | 原因 |
|------|------|------|
| JDK | 21 (LTS) | 本机已有，最新 LTS |
| 构建 | Maven | 国内镜像成熟、资料多 |
| GUI | JavaFX | 现代 UI，CSS/FXML，硬件加速 |
| 依赖注入 | 手动构造器注入 | 桌面应用无需 Spring |
| HTTP 客户端 | java.net.http.HttpClient | JDK 11+ 内置，无需额外依赖 |

### Harness 四要素对应

| 要素 | Java 侧落地 |
|------|-----------|
| Context | `AGENTS.md` 明确 GUI Agent 职责：只负责界面和引擎调度 |
| Constraints | ArchUnit（禁止 UI 层调 FFmpeg、禁止 @Autowired）、Checkstyle（圈复杂度 ≤10、方法 ≤50 行）|
| Feedback | JUnit 5 + Mockito + JaCoCo（≥80%），CI 跑 checkstyle → test → archunit → jacoco |
| Entropy | `mvn verify` 不通过不提交，禁止 TODO/FIXME |

### pom.xml 核心依赖

- `javafx-controls`、`javafx-fxml` — GUI
- `lombok` — 减少样板代码
- `jackson-databind` — JSON 序列化
- `archunit-junit5` (test) — 架构测试
- `junit-jupiter` + `mockito` (test) — 单元测试

### pom.xml 核心插件

- `maven-compiler-plugin` — JDK 21 + Lombok annotation processor
- `maven-checkstyle-plugin` — 代码风格检查
- `jacoco-maven-plugin` — 覆盖率 ≥ 80%
- `maven-surefire-plugin` — 测试运行
- `maven-jar-plugin` — 打包 fat jar

## Python 侧（smartcut-engine）

### 技术选型

| 项目 | 选型 | 对标 Java |
|------|------|----------|
| 运行时 | Python 3.12 | JDK 21 |
| 依赖管理 | Poetry | Maven |
| Web 框架 | FastAPI + uvicorn | 仅本地 HTTP，无对标 |
| 代码检查 | ruff | Checkstyle |
| 类型检查 | mypy (strict) | Java 编译期类型安全 |
| 测试 | pytest + pytest-cov | JUnit + JaCoCo |
| 数据校验 | Pydantic | Jackson |

### 核心依赖

- `fastapi` + `uvicorn` — 本地 HTTP 服务
- `httpx` — 调用厂商 AI API
- `moviepy` — 视频剪辑高级封装
- `ffmpeg-python` — FFmpeg 底层接口
- `openai-whisper` — 语音转文字
- `pydantic` — 数据校验（FastAPI 自带）

### 工程约束（与 Java 侧一致）

- 函数 ≤ 50 行
- 圈复杂度 ≤ 10
- 测试覆盖率 ≥ 80%
- 使用 `logging` 模块，禁止 `print()`
- 禁止裸 `except Exception`

## 项目目录结构

```
SmartCut/
├── smartcut-gui/                    # Java JavaFX 桌面端
│   ├── pom.xml
│   ├── checkstyle.xml
│   └── src/
│       ├── main/java/com/smartcut/
│       │   ├── SmartCutApp.java     # 入口
│       │   ├── ui/                  # 表现层
│       │   │   ├── MainWindow.java
│       │   │   ├── controllers/
│       │   │   └── components/
│       │   ├── service/             # 业务层
│       │   │   ├── EngineClient.java
│       │   │   ├── ProjectService.java
│       │   │   └── ExportService.java
│       │   └── model/               # 数据模型
│       │       ├── Project.java
│       │       ├── VideoTask.java
│       │       └── dto/
│       └── test/java/com/smartcut/
│           ├── ArchitectureTest.java
│           └── service/
│
├── smartcut-engine/                 # Python 视频引擎
│   ├── pyproject.toml
│   ├── ruff.toml
│   └── src/smartcut/
│       ├── __init__.py
│       ├── main.py                  # FastAPI 入口
│       ├── api/
│       │   ├── __init__.py
│       │   ├── router.py
│       │   └── schemas.py
│       ├── services/
│       │   ├── __init__.py
│       │   ├── editor.py
│       │   ├── transcriber.py
│       │   ├── analyzer.py
│       │   └── ai_client.py
│       └── tests/
│
├── docs/
│   ├── AGENTS.md
│   └── superpowers/specs/
├── .github/workflows/
│   └── ci.yml
└── README.md
```

### 层依赖规则

- Java：`ui → service → model`（ui 不直接调 Python 进程）
- Python：`api → services`（api 只做路由和参数校验，不写业务逻辑）

## CI/CD

GitHub Actions，push/PR 触发。并行跑 Java 和 Python 检查：

```yaml
Java Job:
  - checkout
  - setup JDK 21
  - mvn checkstyle:check
  - mvn test
  - mvn jacoco:check

Python Job:
  - checkout
  - setup Python 3.12
  - poetry install
  - ruff check
  - mypy src/
  - pytest --cov --cov-fail-under=80
```

## 不在本次范围

- 数据库配置（用户后续选 MySQL，服务器 119.91.238.231）
- 视频处理/AI 业务逻辑
- 各厂商 AI API 选型
- 打包/分发方案（jpackage、PyInstaller 等）
