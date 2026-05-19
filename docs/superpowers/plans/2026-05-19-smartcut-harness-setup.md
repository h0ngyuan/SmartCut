# SmartCut Harness 工程基础设施实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 从零搭建 SmartCut 双语言（Java 21 + Python 3.12）工程基础设施，包括构建系统、代码检查、架构测试、覆盖率、CI/CD 和目录骨架。

**Architecture:** JavaFX GUI（smartcut-gui）通过 localhost:19527 HTTP 调用 Python FastAPI 引擎（smartcut-engine）。Maven + Poetry 管理依赖，Checkstyle + ruff 做代码检查，ArchUnit + mypy 做架构约束，JaCoCo + pytest-cov 做覆盖率。

**Tech Stack:** JDK 21 + Maven 3.9 + JavaFX + Python 3.12 + Poetry + FastAPI

**前置环境：**
- JDK 21: `C:\Users\24726\.jdks\ms-21.0.10`
- Maven 3.9: `E:\apache-maven-3.9.9`
- Python 3.12.6
- 需安装: Poetry

---

### Task 1: 安装 Poetry

- [ ] **Step 1: 安装 Poetry（Windows PowerShell）**

```powershell
(Invoke-WebRequest -Uri https://install.python-poetry.org -UseBasicParssing).Content | py -
```

- [ ] **Step 2: 验证安装**

```bash
poetry --version
```

Expected: 输出版本号（如 Poetry 2.x）

- [ ] **Step 3: Commit**

```bash
# 无文件变更，仅环境准备
```

---

### Task 2: 创建项目目录结构

**Files:**
- Create: 所有空目录

- [ ] **Step 1: 创建所有目录**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut"

# Java 侧
mkdir -p smartcut-gui/src/main/java/com/smartcut/ui/controllers
mkdir -p smartcut-gui/src/main/java/com/smartcut/ui/components
mkdir -p smartcut-gui/src/main/java/com/smartcut/service
mkdir -p smartcut-gui/src/main/java/com/smartcut/model/dto
mkdir -p smartcut-gui/src/test/java/com/smartcut/service

# Python 侧
mkdir -p smartcut-engine/src/smartcut/api
mkdir -p smartcut-engine/src/smartcut/services
mkdir -p smartcut-engine/tests

# CI/CD
mkdir -p .github/workflows
```

- [ ] **Step 2: 验证目录结构**

```bash
ls -R smartcut-gui/src smartcut-engine .github
```

Expected: 所有目录已创建，无报错。

- [ ] **Step 3: Commit**

```bash
# 空目录无法被 git 追踪，通过 .gitkeep 占位
find smartcut-gui/src -type d -exec touch {}/.gitkeep \;
find smartcut-engine/src -type d -exec touch {}/.gitkeep \;
find smartcut-engine/tests -type d -exec touch {}/.gitkeep \;
git add smartcut-gui/ smartcut-engine/ .github/
git commit -m "scaffold: create project directory structure for Java + Python"
```

---

### Task 3: Java — pom.xml

**Files:**
- Create: `smartcut-gui/pom.xml`

- [ ] **Step 1: 写 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.smartcut</groupId>
    <artifactId>smartcut-gui</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>SmartCut GUI</name>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- 依赖版本 -->
        <javafx.version>21.0.2</javafx.version>
        <lombok.version>1.18.34</lombok.version>
        <jackson.version>2.17.2</jackson.version>
        <archunit.version>1.3.0</archunit.version>
        <junit.version>5.10.3</junit.version>
        <mockito.version>5.12.0</mockito.version>
        <checkstyle.version>10.17.0</checkstyle.version>
        <jacoco.version>0.8.12</jacoco.version>
    </properties>

    <dependencies>
        <!-- JavaFX -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- Jackson (JSON 序列化，与 Python 引擎通信) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.tngtech.archunit</groupId>
            <artifactId>archunit-junit5</artifactId>
            <version>${archunit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Compiler -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!-- Checkstyle -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>${checkstyle.version}</version>
                <configuration>
                    <configLocation>checkstyle.xml</configLocation>
                    <failOnViolation>true</failOnViolation>
                    <violationSeverity>warning</violationSeverity>
                </configuration>
                <executions>
                    <execution>
                        <phase>validate</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Surefire (JUnit 5) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.3.1</version>
            </plugin>

            <!-- JaCoCo -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <!-- 初始阈值 0.0，随业务代码补齐后上调至 0.80 -->
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.0</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 验证 Maven 编译（空项目，预期成功无测试）**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn compile -q
```

Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add smartcut-gui/pom.xml
git commit -m "build: add Maven pom.xml with JavaFX, Checkstyle, ArchUnit, JaCoCo"
```

---

### Task 4: Java — checkstyle.xml

**Files:**
- Create: `smartcut-gui/checkstyle.xml`

- [ ] **Step 1: 写 checkstyle.xml**

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <property name="charset" value="UTF-8"/>
    <property name="severity" value="warning"/>

    <module name="FileLength">
        <property name="max" value="500"/>
    </module>

    <module name="TreeWalker">
        <!-- 圈复杂度 ≤ 10 -->
        <module name="CyclomaticComplexity">
            <property name="max" value="10"/>
        </module>

        <!-- 方法不超过 50 行 -->
        <module name="MethodLength">
            <property name="max" value="50"/>
        </module>

        <!-- 命名规范 -->
        <module name="PackageName">
            <property name="format" value="^[a-z]+(\.[a-z][a-z0-9]*)*$"/>
        </module>
        <module name="TypeName">
            <property name="format" value="^[A-Z][a-zA-Z0-9]*$"/>
        </module>
        <module name="MethodName">
            <property name="format" value="^[a-z][a-zA-Z0-9]*$"/>
        </module>
        <module name="ConstantName">
            <property name="format" value="^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"/>
        </module>

        <!-- 禁止 import sun.* 内部 API -->
        <module name="IllegalImport">
            <property name="illegalPkgs" value="sun.*"/>
        </module>

        <!-- 禁止未使用的 import -->
        <module name="UnusedImports"/>

        <!-- import 顺序 -->
        <module name="ImportOrder">
            <property name="groups" value="java,javafx,javax,org,com"/>
            <property name="ordered" value="true"/>
            <property name="separated" value="true"/>
        </module>

        <!-- 禁止 System.out / System.err -->
        <module name="RegexpSinglelineJava">
            <property name="format" value="System\.(out|err)\."/>
            <property name="message" value="Use SLF4J/logger instead of System.out/err"/>
        </module>

        <!-- 禁止 printStackTrace -->
        <module name="RegexpSinglelineJava">
            <property name="format" value="\.printStackTrace\(\)"/>
            <property name="message" value="Use proper logging instead of printStackTrace()"/>
        </module>
    </module>
</module>
```

- [ ] **Step 2: 验证 checkstyle 可运行**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn checkstyle:check
```

Expected: BUILD SUCCESS（空 src 无违规）

- [ ] **Step 3: Commit**

```bash
git add smartcut-gui/checkstyle.xml
git commit -m "lint: add Checkstyle rules (CCN≤10, method≤50, no System.out)"
```

---

### Task 5: Java — ArchUnit 架构测试

**Files:**
- Create: `smartcut-gui/src/test/java/com/smartcut/ArchitectureTest.java`

- [ ] **Step 1: 写 ArchitectureTest.java**

```java
package com.smartcut;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.smartcut", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    // 禁止循环依赖
    @ArchTest
    public static final ArchRule no_cycles = slices()
        .matching("com.smartcut.(*)..")
        .should().beFreeOfCycles();

    // service 层不依赖 ui 层
    @ArchTest
    public static final ArchRule service_should_not_depend_on_ui =
        noClasses()
            .that().resideInAPackage("com.smartcut.service..")
            .should().dependOnClassesThat().resideInAPackage("com.smartcut.ui..");

    // model 层不依赖 service 和 ui 层
    @ArchTest
    public static final ArchRule model_should_not_depend_on_upper_layers =
        noClasses()
            .that().resideInAPackage("com.smartcut.model..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("com.smartcut.service..", "com.smartcut.ui..");
}
```

- [ ] **Step 2: 运行 ArchUnit 测试验证**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn test -Dtest=ArchitectureTest
```

Expected: BUILD SUCCESS（空项目无违规）

- [ ] **Step 3: Commit**

```bash
git add smartcut-gui/src/test/java/com/smartcut/ArchitectureTest.java
git commit -m "test: add ArchUnit architecture rules (no cycles, layer constraints)"
```

---

### Task 6: Java — 数据模型类

**Files:**
- Create: `smartcut-gui/src/main/java/com/smartcut/model/VideoTask.java`
- Create: `smartcut-gui/src/main/java/com/smartcut/model/Project.java`
- Create: `smartcut-gui/src/main/java/com/smartcut/model/dto/EngineRequest.java`
- Create: `smartcut-gui/src/main/java/com/smartcut/model/dto/EngineResponse.java`

- [ ] **Step 1: 写 VideoTask.java**

```java
package com.smartcut.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class VideoTask {

    public enum Status { PENDING, PROCESSING, DONE, FAILED }

    private final UUID id;
    private final Path sourceVideo;
    private final String userPrompt;
    private Status status;
    private Path resultVideo;
    private String errorMessage;
    private final LocalDateTime createdAt;

    public VideoTask(Path sourceVideo, String userPrompt) {
        this.id = UUID.randomUUID();
        this.sourceVideo = sourceVideo;
        this.userPrompt = userPrompt;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    @JsonCreator
    public VideoTask(
            @JsonProperty("id") UUID id,
            @JsonProperty("sourceVideo") Path sourceVideo,
            @JsonProperty("userPrompt") String userPrompt,
            @JsonProperty("status") Status status,
            @JsonProperty("resultVideo") Path resultVideo,
            @JsonProperty("errorMessage") String errorMessage,
            @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.sourceVideo = sourceVideo;
        this.userPrompt = userPrompt;
        this.status = status;
        this.resultVideo = resultVideo;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    public void markProcessing() { this.status = Status.PROCESSING; }
    public void markDone(Path result) { this.status = Status.DONE; this.resultVideo = result; }
    public void markFailed(String error) { this.status = Status.FAILED; this.errorMessage = error; }
}
```

- [ ] **Step 2: 写 Project.java**

```java
package com.smartcut.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Project {

    private final UUID id;
    private final String name;
    private final List<VideoTask> tasks;

    public Project(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public void addTask(VideoTask task) {
        this.tasks.add(task);
    }

    public List<VideoTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
```

- [ ] **Step 3: 写 EngineRequest.java**

```java
package com.smartcut.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EngineRequest {

    private final String action;
    private final String sourcePath;
    private final String prompt;

    public EngineRequest(
            @JsonProperty("action") String action,
            @JsonProperty("sourcePath") String sourcePath,
            @JsonProperty("prompt") String prompt) {
        this.action = action;
        this.sourcePath = sourcePath;
        this.prompt = prompt;
    }
}
```

- [ ] **Step 4: 写 EngineResponse.java**

```java
package com.smartcut.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EngineResponse {

    public enum Status { SUCCESS, ERROR }

    private final Status status;
    private final String resultPath;
    private final String message;

    @JsonCreator
    public EngineResponse(
            @JsonProperty("status") Status status,
            @JsonProperty("resultPath") String resultPath,
            @JsonProperty("message") String message) {
        this.status = status;
        this.resultPath = resultPath;
        this.message = message;
    }

    public static EngineResponse success(String path) {
        return new EngineResponse(Status.SUCCESS, path, null);
    }

    public static EngineResponse error(String message) {
        return new EngineResponse(Status.ERROR, null, message);
    }
}
```

- [ ] **Step 5: 编译验证**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add smartcut-gui/src/main/java/com/smartcut/model/
git commit -m "feat: add data model classes (Project, VideoTask, Engine DTOs)"
```

---

### Task 7: Java — EngineClient 与单元测试

**Files:**
- Create: `smartcut-gui/src/main/java/com/smartcut/service/EngineClient.java`
- Create: `smartcut-gui/src/test/java/com/smartcut/service/EngineClientTest.java`

- [ ] **Step 1: 写 EngineClientTest.java（先写测试）**

```java
package com.smartcut.service;

import com.smartcut.model.dto.EngineRequest;
import com.smartcut.model.dto.EngineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineClientTest {

    private EngineClient client;

    @BeforeEach
    void setUp() {
        client = new EngineClient("http://localhost:19527");
    }

    @Test
    void healthCheck_engineRunning_returnsTrue() {
        // 引擎未启动时预期 false，不抛异常即为正确行为
        boolean result = client.healthCheck();
        assertFalse(result);
    }

    @Test
    void getBaseUrl_returnsConfiguredUrl() {
        assertEquals("http://localhost:19527", client.getBaseUrl());
    }

    @Test
    void buildRequest_createsValidJson() {
        EngineRequest request = new EngineRequest("cut", "/path/to/video.mp4", "剪出精彩片段");
        assertNotNull(request);
        assertEquals("cut", request.getAction());
        assertEquals("/path/to/video.mp4", request.getSourcePath());
        assertEquals("剪出精彩片段", request.getPrompt());
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn test -Dtest=EngineClientTest
```

Expected: 编译失败（EngineClient 类不存在）

- [ ] **Step 3: 写 EngineClient.java**

```java
package com.smartcut.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcut.model.dto.EngineRequest;
import com.smartcut.model.dto.EngineResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class EngineClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EngineClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean healthCheck() {
        try {
            var request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/health"))
                .GET()
                .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public EngineResponse submitTask(EngineRequest taskRequest) {
        try {
            var json = objectMapper.writeValueAsString(taskRequest);
            var request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/task"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), EngineResponse.class);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return EngineResponse.error("Engine communication failed: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 4: 运行测试验证通过**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn test -Dtest=EngineClientTest
```

Expected: Tests PASS（healthCheck 返回 false，因为引擎未启动——这是正确的行为）

- [ ] **Step 5: Commit**

```bash
git add smartcut-gui/src/main/java/com/smartcut/service/EngineClient.java smartcut-gui/src/test/java/com/smartcut/service/EngineClientTest.java
git commit -m "feat: add EngineClient for Python engine HTTP communication"
```

---

### Task 8: Java — UI 骨架与 Service 桩

**Files:**
- Create: `smartcut-gui/src/main/java/com/smartcut/ui/package-info.java`
- Create: `smartcut-gui/src/main/java/com/smartcut/service/package-info.java`
- Create: `smartcut-gui/src/main/java/com/smartcut/model/package-info.java`

- [ ] **Step 1: 写 ui/package-info.java**

```java
/**
 * UI 层 — JavaFX 界面。
 * 仅负责视图和用户交互，不直接调用 Python 进程或 FFmpeg。
 * 通过 {@link com.smartcut.service.EngineClient} 与引擎通信。
 */
package com.smartcut.ui;
```

- [ ] **Step 2: 写 service/package-info.java**

```java
/**
 * 业务服务层 — 编排 UI 与引擎之间的交互。
 * 不直接操作 JavaFX 组件，不直接调用 Python 进程。
 */
package com.smartcut.service;
```

- [ ] **Step 3: 写 model/package-info.java**

```java
/**
 * 数据模型层 — 纯数据结构，无业务逻辑。
 * 不依赖 ui 和 service 层。
 */
package com.smartcut.model;
```

- [ ] **Step 4: 编译验证**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add smartcut-gui/src/main/java/com/smartcut/ui/ smartcut-gui/src/main/java/com/smartcut/service/
git commit -m "docs: add package-info.java describing layer responsibilities"
```

---

### Task 9: Java — 应用入口 SmartCutApp

**Files:**
- Create: `smartcut-gui/src/main/java/com/smartcut/SmartCutApp.java`
- Create: `smartcut-gui/src/main/java/com/smartcut/ui/MainWindow.java`

- [ ] **Step 1: 写 SmartCutApp.java**

```java
package com.smartcut;

import com.smartcut.service.EngineClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class SmartCutApp extends Application {

    private static final Logger LOG = Logger.getLogger(SmartCutApp.class.getName());
    private static final int ENGINE_PORT = 19527;

    private Process engineProcess;
    private EngineClient engineClient;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        startEngine();
        engineClient = new EngineClient("http://localhost:" + ENGINE_PORT);

        primaryStage.setTitle("SmartCut - 智能视频剪辑");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> stopEngine());
    }

    private void startEngine() {
        try {
            var engineDir = System.getProperty("smartcut.engine.dir", "../smartcut-engine");
            var processBuilder = new ProcessBuilder("poetry", "run", "uvicorn", "src.smartcut.main:app",
                "--host", "127.0.0.1", "--port", String.valueOf(ENGINE_PORT));
            processBuilder.directory(new java.io.File(engineDir));
            processBuilder.redirectErrorStream(true);
            engineProcess = processBuilder.start();
            LOG.info("Python engine started on port " + ENGINE_PORT);
        } catch (IOException e) {
            LOG.severe("Failed to start Python engine: " + e.getMessage());
        }
    }

    private void stopEngine() {
        if (engineProcess != null && engineProcess.isAlive()) {
            engineProcess.destroy();
            LOG.info("Python engine stopped");
        }
    }

    @Override
    public void stop() {
        stopEngine();
    }
}
```

- [ ] **Step 2: 写 MainWindow.java（桩）**

```java
package com.smartcut.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainWindow {

    public Scene createScene() {
        var pane = new StackPane();
        pane.getChildren().add(new Label("SmartCut - 智能视频剪辑"));
        return new Scene(pane, 1200, 800);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add smartcut-gui/src/main/java/com/smartcut/SmartCutApp.java smartcut-gui/src/main/java/com/smartcut/ui/MainWindow.java
git commit -m "feat: add JavaFX application entry point with engine lifecycle"
```

---

### Task 10: Python — pyproject.toml

**Files:**
- Create: `smartcut-engine/pyproject.toml`

- [ ] **Step 1: 写 pyproject.toml**

```toml
[tool.poetry]
name = "smartcut-engine"
version = "0.1.0"
description = "SmartCut video processing engine"
authors = ["hyuans <2472613445@qq.com>"]
readme = "README.md"
package-mode = false

[tool.poetry.dependencies]
python = "^3.12"
fastapi = "^0.115"
uvicorn = { version = "^0.32", extras = ["standard"] }
httpx = "^0.28"
moviepy = "^2.0"
ffmpeg-python = "^0.2"
openai-whisper = "^20240930"
pydantic = "^2.10"

[tool.poetry.group.dev.dependencies]
pytest = "^8.3"
pytest-cov = "^6.0"
ruff = "^0.8"
mypy = "^1.13"

[build-system]
requires = ["poetry-core"]
build-backend = "poetry.core.masonry.api"

[tool.ruff]
line-length = 100
target-version = "py312"

[tool.ruff.lint]
select = [
    "E",   # pycodestyle errors
    "F",   # pyflakes
    "I",   # isort
    "N",   # pep8-naming
    "W",   # pycodestyle warnings
    "UP",  # pyupgrade
    "B",   # flake8-bugbear
    "C4",  # flake8-comprehensions
    "SIM", # flake8-simplify
]
ignore = []

[tool.ruff.lint.per-file-ignores]
"tests/**/*.py" = ["S101"]  # allow assert in tests

[tool.ruff.format]
quote-style = "double"
indent-style = "space"

[tool.pytest.ini_options]
testpaths = ["tests"]
pythonpath = ["src"]

[tool.coverage.run]
source = ["src/smartcut"]
branch = true

[tool.coverage.report]
fail_under = 80
show_missing = true

[tool.mypy]
strict = true
ignore_missing_imports = true
exclude = ["tests/"]
```

- [ ] **Step 2: 安装依赖**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry install
```

Expected: 依赖安装成功，无报错

- [ ] **Step 3: Commit**

```bash
git add smartcut-engine/pyproject.toml smartcut-engine/poetry.lock
git commit -m "build: add Python Poetry config with FastAPI, MoviePy, Whisper deps"
```

---

### Task 11: Python — FastAPI 入口与 API 路由

**Files:**
- Create: `smartcut-engine/src/smartcut/__init__.py`
- Create: `smartcut-engine/src/smartcut/main.py`
- Create: `smartcut-engine/src/smartcut/api/__init__.py`
- Create: `smartcut-engine/src/smartcut/api/router.py`
- Create: `smartcut-engine/src/smartcut/api/schemas.py`

- [ ] **Step 1: 写 __init__.py 文件**

```bash
echo '"""SmartCut Engine - AI-powered video processing."""' > smartcut-engine/src/smartcut/__init__.py
echo '"""API layer - HTTP routes and request/response schemas."""' > smartcut-engine/src/smartcut/api/__init__.py
```

- [ ] **Step 2: 写 schemas.py**

```python
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
```

- [ ] **Step 3: 写 router.py**

```python
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
```

- [ ] **Step 4: 写 main.py**

```python
"""FastAPI application entry point for the SmartCut engine."""

from fastapi import FastAPI

from .api.router import router

app = FastAPI(
    title="SmartCut Engine",
    version="0.1.0",
    description="Video processing engine with AI capabilities",
)

app.include_router(router)
```

- [ ] **Step 5: 启动引擎验证**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run uvicorn src.smartcut.main:app --host 127.0.0.1 --port 19527 &
sleep 3
curl http://localhost:19527/api/health
kill %1 2>/dev/null
```

Expected: 返回 `{"status":"ok","version":"0.1.0"}`

- [ ] **Step 6: Commit**

```bash
git add smartcut-engine/src/smartcut/
git commit -m "feat: add Python FastAPI engine with health and task endpoints"
```

---

### Task 12: Python — 服务层桩代码

**Files:**
- Create: `smartcut-engine/src/smartcut/services/__init__.py`
- Create: `smartcut-engine/src/smartcut/services/editor.py`
- Create: `smartcut-engine/src/smartcut/services/transcriber.py`
- Create: `smartcut-engine/src/smartcut/services/analyzer.py`
- Create: `smartcut-engine/src/smartcut/services/ai_client.py`

- [ ] **Step 1: 写 services/__init__.py**

```python
"""Business logic layer — video processing, AI analysis, transcription."""
```

- [ ] **Step 2: 写 editor.py**

```python
"""Video editing orchestration — cut, merge, speed, effects via MoviePy/FFmpeg."""

import logging

logger = logging.getLogger(__name__)


class VideoEditor:
    """Orchestrates video editing operations."""

    def process(self, source_path: str, action: str, prompt: str) -> str:
        """Stub — returns the source path unchanged.

        Args:
            source_path: Absolute path to the input video.
            action: Operation type (cut, highlight, subtitle, speed).
            prompt: Natural language instruction.

        Returns:
            Path to the processed video file.
        """
        logger.info("Processing video: source=%s action=%s", source_path, action)
        raise NotImplementedError("VideoEditor.process not yet implemented")
```

- [ ] **Step 3: 写 transcriber.py**

```python
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
```

- [ ] **Step 4: 写 analyzer.py**

```python
"""Video content analysis — scene detection, highlight scoring, object detection."""

import logging

logger = logging.getLogger(__name__)


class VideoAnalyzer:
    """Analyzes video content to identify highlights, scenes, and key moments."""

    def find_highlights(self, video_path: str, prompt: str) -> list[tuple[float, float]]:
        """Stub — find highlight segments based on prompt.

        Args:
            video_path: Path to the video file.
            prompt: User's natural language description of desired highlights.

        Returns:
            List of (start_seconds, end_seconds) tuples.
        """
        logger.info("Finding highlights: %s prompt=%s", video_path, prompt)
        raise NotImplementedError("VideoAnalyzer.find_highlights not yet implemented")
```

- [ ] **Step 5: 写 ai_client.py**

```python
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
```

- [ ] **Step 6: 编译/语法检查**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run python -c "from src.smartcut.services.editor import VideoEditor; print('OK')"
```

Expected: 输出 `OK`

- [ ] **Step 7: Commit**

```bash
git add smartcut-engine/src/smartcut/services/
git commit -m "feat: add Python service stubs (editor, transcriber, analyzer, ai_client)"
```

---

### Task 13: Python — 引擎测试

**Files:**
- Create: `smartcut-engine/tests/__init__.py`
- Create: `smartcut-engine/tests/test_health.py`

- [ ] **Step 1: 写 tests/__init__.py**

```bash
echo '# SmartCut engine tests' > smartcut-engine/tests/__init__.py
```

- [ ] **Step 2: 写 test_health.py**

```python
"""Tests for the health endpoint and API schemas."""

import pytest
from fastapi.testclient import TestClient

from src.smartcut.main import app
from src.smartcut.api.schemas import HealthResponse, TaskRequest, TaskResponse


class TestHealthEndpoint:
    """Health check endpoint tests."""

    @pytest.fixture
    def client(self):
        return TestClient(app)

    def test_health_returns_ok(self, client):
        response = client.get("/api/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "ok"
        assert data["version"] == "0.1.0"

    def test_task_endpoint_returns_stub_error(self, client):
        response = client.post("/api/task", json={
            "action": "cut",
            "source_path": "/tmp/test.mp4",
            "prompt": "剪出精彩片段"
        })
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "ERROR"


class TestSchemas:
    """Pydantic schema validation tests."""

    def test_health_response_defaults(self):
        hr = HealthResponse()
        assert hr.status == "ok"
        assert hr.version == "0.1.0"

    def test_task_request_valid(self):
        tr = TaskRequest(action="cut", source_path="/v/test.mp4", prompt="剪一下")
        assert tr.action == "cut"

    def test_task_request_missing_field(self):
        with pytest.raises(Exception):
            TaskRequest(action="cut")  # missing source_path and prompt

    def test_task_response_success(self):
        tr = TaskResponse(status="SUCCESS", result_path="/out.mp4")
        assert tr.status == "SUCCESS"
        assert tr.result_path == "/out.mp4"

    def test_task_response_error(self):
        tr = TaskResponse(status="ERROR", message="Something went wrong")
        assert tr.status == "ERROR"
        assert tr.message == "Something went wrong"
```

- [ ] **Step 3: 运行测试验证通过**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run pytest tests/ -v
```

Expected: 所有测试 PASS

- [ ] **Step 4: 运行 ruff 检查**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run ruff check src/ tests/
```

Expected: 无违规（All checks passed!）

- [ ] **Step 5: 运行 mypy 检查**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run mypy src/
```

Expected: 无类型错误（Success: no issues found）

- [ ] **Step 6: 运行覆盖率检查**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run pytest tests/ --cov=src/smartcut --cov-report=term --cov-fail-under=80
```

Expected: 覆盖率 ≥ 80%，所有测试 PASS

- [ ] **Step 7: Commit**

```bash
git add smartcut-engine/tests/
git commit -m "test: add Python engine tests for health endpoint and schemas"
```

---

### Task 14: CI/CD — GitHub Actions

**Files:**
- Create: `.github/workflows/ci.yml`

- [ ] **Step 1: 写 ci.yml**

```yaml
name: CI

on:
  push:
    branches: [master]
  pull_request:
    branches: [master]

jobs:
  java:
    name: Java (smartcut-gui)
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: smartcut-gui
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: "21"
          distribution: "temurin"
          cache: maven

      - name: Run Checkstyle
        run: mvn checkstyle:check

      - name: Run Tests
        run: mvn test

      - name: Run Jacoco Coverage Check
        run: mvn jacoco:check

  python:
    name: Python (smartcut-engine)
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: smartcut-engine
    steps:
      - uses: actions/checkout@v4

      - name: Set up Python 3.12
        uses: actions/setup-python@v5
        with:
          python-version: "3.12"

      - name: Install Poetry
        run: pip install poetry

      - name: Install dependencies
        run: poetry install --with dev

      - name: Run Ruff
        run: poetry run ruff check src/ tests/

      - name: Run Mypy
        run: poetry run mypy src/

      - name: Run Tests with Coverage
        run: poetry run pytest tests/ --cov=src/smartcut --cov-fail-under=80
```

- [ ] **Step 2: Commit**

```bash
git add .github/workflows/ci.yml
git commit -m "ci: add GitHub Actions workflow for Java + Python parallel checks"
```

---

### Task 15: docs/AGENTS.md

**Files:**
- Create: `docs/AGENTS.md`

- [ ] **Step 1: 写 AGENTS.md**

```markdown
# Agent 职责说明

## Java GUI Agent

- **负责**: SmartCut 桌面 GUI 开发
- **技术栈**: JDK 21, JavaFX, Maven, Jackson
- **文件路径**: `smartcut-gui/src/**`
- **职责边界**:
  - UI 视图和用户交互（JavaFX）
  - 通过 EngineClient 调度 Python 引擎
  - 项目管理（Project/VideoTask）
  - 视频预览和导出界面
- **禁止**:
  - 直接调用 FFmpeg 或 Python 进程（必须通过 EngineClient）
  - 在 UI 层写视频处理/AI 分析逻辑
  - 修改 Python 引擎代码

## Python Engine Agent

- **负责**: 视频处理引擎
- **技术栈**: Python 3.12, FastAPI, MoviePy, FFmpeg, Whisper
- **文件路径**: `smartcut-engine/src/**`
- **职责边界**:
  - 视频剪辑/合成（MoviePy + FFmpeg）
  - AI API 调用（视觉分析、NLP）
  - 语音识别和字幕生成（Whisper）
  - FastAPI 路由和请求校验
- **禁止**:
  - 修改 Java GUI 代码
  - 处理 GUI 状态/逻辑
  - 直接操作用户文件系统（必须通过 API 传入路径）

## 跨层规则

- Java ↔ Python 通信仅通过 `localhost:19527` HTTP + JSON
- JSON schema 变更必须两边同步更新（`EngineRequest/EngineResponse` ↔ `TaskRequest/TaskResponse`）
- 两边的测试必须独立可运行（不依赖对方启动）
```

- [ ] **Step 2: Commit**

```bash
git add docs/AGENTS.md
git commit -m "docs: add AGENTS.md defining agent responsibilities"
```

---

### Task 16: 全链路验证

- [ ] **Step 1: 验证 Java 侧完整流程**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-gui"
export JAVA_HOME="C:\Users\24726\.jdks\ms-21.0.10"
mvn clean verify
```

Expected: checkstyle 通过 → 测试通过 → archunit 通过 → jacoco 报告生成（初始阈值 0.0，后续上调至 0.80）

- [ ] **Step 2: 验证 Python 侧完整流程**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run ruff check src/ tests/
poetry run mypy src/
poetry run pytest tests/ -v
```

Expected: 三项全部通过

- [ ] **Step 3: 验证引擎可启动**

```bash
cd "E:\IDE\IDEA\IDEproject\SmartCut\smartcut-engine"
poetry run uvicorn src.smartcut.main:app --host 127.0.0.1 --port 19527 &
ENGINE_PID=$!
sleep 2
curl -s http://localhost:19527/api/health
curl -s -X POST http://localhost:19527/api/task \
  -H "Content-Type: application/json" \
  -d '{"action":"cut","source_path":"/tmp/test.mp4","prompt":"test"}'
kill $ENGINE_PID 2>/dev/null
```

Expected: health 返回 `{"status":"ok","version":"0.1.0"}`，task 返回 `{"status":"ERROR","message":"Action 'cut' not yet implemented"}`

- [ ] **Step 4: 最终 commit**

```bash
git add .
git commit -m "verify: full Harness validation — Java + Python all checks pass"
```
