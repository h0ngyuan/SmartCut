# SmartCut Tauri 迁移设计

日期：2026-05-20

## 范围

将 SmartCut 从 JavaFX GUI + Python 引擎的双语言架构，迁移到 Tauri + React（前端）+ Python FastAPI（主力引擎）+ Java（可选储备服务）的三语言架构。本文档覆盖架构重组、目录迁移、组件设计和子进程管理。不覆盖具体业务功能实现。

## 动机

- **学习目标**：扩展技术栈，从纯 Java/Spring 生态扩展到 Rust（系统编程）、TypeScript/React（现代前端）、Python AI 应用
- **技术原因**：JavaFX 桌面开发生态薄弱，Tauri 提供更好的窗口管理、系统集成和前端生态
- **保留 Java**：不作为热路径，但作为可选服务保留，用于数据持久化、复杂业务逻辑、企业集成

## 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                   Tauri Shell (Rust)                         │
│  窗口管理 · 系统托盘 · 文件对话框 · 子进程生命周期            │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         React Frontend (TypeScript)                   │   │
│  │  全部 UI · invoke() 调 Rust · fetch() 调后端          │   │
│  └──────────────────────────────────────────────────────┘   │
│         │ fetch()                    │ fetch() (按需)        │
│         ▼                            ▼                       │
│  ┌──────────────┐          ┌──────────────────┐             │
│  │ Python Engine │          │  Java Service     │             │
│  │ :19527        │          │  :19528           │             │
│  │ 主力后端       │          │  可选储备         │             │
│  │ 视频处理·AI   │          │  持久化·企业集成   │             │
│  └──────────────┘          └──────────────────┘             │
└─────────────────────────────────────────────────────────────┘
```

### 通信路径

| 谁 ↔ 谁 | 协议 | 用途 |
|---------|------|------|
| React → Python | HTTP REST (fetch) | 视频处理、AI 分析、字幕生成 |
| React → Java | HTTP REST (fetch) | 数据持久化、复杂业务（按需） |
| React → Rust | Tauri invoke() / IPC | 文件对话框、系统菜单、窗口操作 |
| Rust → Python/Java | std::process::Command | 子进程生命周期管理 |

### 启动时序

1. Tauri `main()` 构建 app，注册所有 commands
2. `setup` hook 中启动 Python 子进程（`poetry run uvicorn`）
3. 轮询 `GET /api/health` 直到 200 OK（最多 10 秒）
4. Java 不启动（按需由前端调用 `start_java_service` command）
5. 创建 1200×800 窗口，加载 React 前端
6. `on_window_event(Destroyed)` → 依次 kill Java → kill Python

## Tauri Rust 层

**职责：纯胶水，不写业务逻辑**

### 文件结构

```
src-tauri/
├── Cargo.toml
├── tauri.conf.json
└── src/
    ├── main.rs         入口，注册 commands
    ├── engine.rs       Python 子进程管理
    └── commands.rs     前端可调用的 Tauri commands
```

### 依赖

```toml
[dependencies]
tauri = { version = "2", features = ["tray-icon"] }
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["process", "time"] }
reqwest = { version = "0.12", features = ["json"] }
```

### engine.rs — 子进程管理

```rust
pub struct EngineGuard {
    child: std::process::Child,
    port: u16,
}

impl EngineGuard {
    pub fn start(engine_dir: &PathBuf, port: u16) -> Result<Self>;
    pub async fn wait_ready(&self) -> Result<()>;  // 轮询 /api/health
    pub fn stop(&mut self);                         // SIGTERM → SIGKILL
}
```

### commands.rs — 前端可调用的命令

| Command | 返回 | 用途 |
|---------|------|------|
| `engine_status()` | `{ python, java }` status | StatusBar 轮询 |
| `pick_video_file()` | `Option<String>` | 文件打开对话框 |
| `pick_output_dir()` | `Option<String>` | 目录选择对话框 |
| `start_java_service()` | `()` | 按需启动 Java |
| `get_app_version()` | `String` | 关于对话框 |

## React 前端

### 技术选型

| 层 | 选型 | 理由 |
|----|------|------|
| UI 框架 | React 18 + TypeScript | 用户选择 |
| 构建工具 | Vite | Tauri 官方推荐 |
| 状态管理 | 初期 useState/useReducer | YAGNI，复杂后上 Zustand |
| 样式 | Tailwind CSS | 快速开发 |
| HTTP | fetch（浏览器原生） | 零依赖，桌面环境无需 axios |
| Tauri API | @tauri-apps/api | 文件对话框等 |

### 组件树（初始版本）

```
App
├── TitleBar              Tauri 原生窗口装饰
├── Layout
│   ├── Sidebar
│   │   ├── ImportButton  调 Rust pick_video_file
│   │   └── ProjectTree   文件/项目列表（桩）
│   ├── MainContent
│   │   ├── VideoPreview  视频播放区（桩）
│   │   └── TaskPanel     剪辑指令输入 + 任务状态
│   └── StatusBar
│       ├── EngineIndicator  Python 连接状态（绿/红）
│       └── JavaIndicator    Java 服务状态
```

### 桌面布局（1200×800）

- **侧边栏** (200px)：导入按钮、项目文件列表
- **主区域** (flex)：视频预览，拖拽导入区域
- **底栏**：引擎状态指示灯（Python / Java）

## Python 引擎

**改动：零。** `smartcut-engine/` 目录、代码、API 协议完全不变。

已有端点：
- `GET /api/health` → `{"status":"ok","version":"0.1.0"}`
- `POST /api/task` → `{"status":"ERROR","message":"..."}`（桩，等待业务逻辑）

唯一的外部变化：启动者从 Java `ProcessBuilder` 变为 Rust `std::process::Command`。

## Java 服务（储备）

### 改造方案：smartcut-gui → smartcut-service

| 文件 | 动作 | 原因 |
|------|------|------|
| `SmartCutApp.java` | 删除 | 启动职责移到 Tauri Rust |
| `ui/MainWindow.java` | 删除 | UI 由 React 接管 |
| `ui/package-info.java` | 删除 | ui 包整体移除 |
| `ui/controllers/` | 删除 | 空目录 |
| `ui/components/` | 删除 | 空目录 |
| `javafx-*` Maven 依赖 | 删除 | 不再需要 JavaFX |
| `EngineClient.java` | 保留 | 后续 Java ↔ Python 通信 |
| `model/Project.java` | 保留 | 核心数据模型 |
| `model/VideoTask.java` | 保留 | 核心数据模型 |
| `model/dto/EngineRequest.java` | 保留 | Python 通信 DTO |
| `model/dto/EngineResponse.java` | 保留 | Python 通信 DTO |
| `ArchitectureTest.java` | 更新 | 去掉 ui 层，只约束 service/model |
| `checkstyle.xml` | 保留 | 代码规范不变 |
| `pom.xml` | 更新 | 移除 JavaFX 依赖，可加 Spring Boot |

### Java 的定位

- 独立可选服务，端口 19528
- 前端直接调 Python，不经过 Java
- 当需要数据库持久化、复杂业务规则、或对接企业内部 Java 系统时，由 Rust 按需启动
- 可选的 Spring Boot 集成（技术学习目的）

## 项目目录结构

```
SmartCut/
├── smartcut-frontend/         # NEW: Tauri + React
│   ├── src-tauri/             Rust 内核
│   │   ├── Cargo.toml
│   │   ├── tauri.conf.json
│   │   └── src/
│   │       ├── main.rs
│   │       ├── engine.rs
│   │       └── commands.rs
│   ├── src/                   React 前端
│   │   ├── App.tsx
│   │   ├── components/
│   │   └── main.tsx
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
├── smartcut-engine/           # KEPT: Python FastAPI（不变）
│   ├── pyproject.toml
│   ├── poetry.lock
│   └── src/smartcut/
│       ├── main.py
│       ├── api/
│       │   ├── router.py
│       │   └── schemas.py
│       └── services/
│           ├── editor.py
│           ├── transcriber.py
│           ├── analyzer.py
│           └── ai_client.py
│
├── smartcut-service/          # RENAMED: smartcut-gui → smartcut-service
│   ├── pom.xml                # 移除 JavaFX
│   ├── checkstyle.xml
│   └── src/
│       ├── main/java/com/smartcut/
│       │   ├── model/
│       │   │   ├── Project.java
│       │   │   ├── VideoTask.java
│       │   │   ├── dto/EngineRequest.java
│       │   │   └── dto/EngineResponse.java
│       │   └── service/
│       │       └── EngineClient.java
│       └── test/java/com/smartcut/
│           ├── ArchitectureTest.java
│           └── service/EngineClientTest.java
│
├── docs/
│   ├── AGENTS.md              # 更新：增加 Tauri Agent
│   └── superpowers/
│       ├── specs/
│       └── plans/
├── .github/workflows/
│   └── ci.yml                 # 扩展：增加 frontend job
└── CLAUDE.md                  # 更新
```

## CI/CD 扩展

在现有 Java + Python 两个 job 基础上，增加第三个：

```yaml
frontend:
  name: Frontend (smartcut-frontend)
  runs-on: ubuntu-latest
  defaults:
    run:
      working-directory: smartcut-frontend
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-node@v4
      with:
        node-version: "22"
    - run: npm ci
    - run: npm run lint
    - run: npm run build
    - run: npx tauri build --ci  # 验证 Tauri 编译通过
```

## AGENTS.md 更新

新增 Tauri Agent 职责：

```
## Tauri Agent

- **负责**：SmartCut 桌面壳和前端开发
- **技术栈**：Rust, Tauri 2, React 18, TypeScript, Vite
- **文件路径**：`smartcut-frontend/src/**`, `smartcut-frontend/src-tauri/**`
- **职责边界**：
  - 窗口管理和系统集成
  - 前端 UI 全部交互
  - Python/Java 子进程生命周期
  - 暴露系统 API 给前端（文件对话框等）
- **禁止**：
  - 写视频处理逻辑（属于 Python）
  - 写数据库操作（属于 Java）
  - 修改 Python 引擎代码
  - 修改 Java 服务代码
```

Java Agent 职责更新：从 "GUI 开发" 改为 "可选后端服务开发"。

## 不在本次范围

- 具体业务功能（视频剪辑、AI 分析）
- 数据库设计与实现
- AI 厂商 API 选型
- 应用打包分发（`.msi`/`.dmg`/`.AppImage`）
- 自动更新机制
