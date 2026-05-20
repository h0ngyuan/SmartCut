package com.smartcut.ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainWindow {

    public Scene createScene() {
        var pane = new StackPane();
        pane.getChildren().add(new Label("SmartCut"));
        return new Scene(pane, 1200, 800);
    }
}
