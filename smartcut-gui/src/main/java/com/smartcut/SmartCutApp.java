package com.smartcut;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;

import com.smartcut.service.EngineClient;

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

        primaryStage.setTitle("SmartCut");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> stopEngine());
    }

    private void startEngine() {
        try {
            var engineDir = System.getProperty("smartcut.engine.dir", "../smartcut-engine");
            var processBuilder = new ProcessBuilder("poetry", "run", "uvicorn",
                "src.smartcut.main:app",
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
