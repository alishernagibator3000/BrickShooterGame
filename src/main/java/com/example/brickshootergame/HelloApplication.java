package com.example.brickshootergame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private static final int SCENE_WIDTH = 750;
    private static final int SCENE_HEIGHT = 500;

    private GameController controller;

    @Override
    public void start(Stage primaryStage) {
        controller = new GameController(SCENE_WIDTH, SCENE_HEIGHT);
        controller.initialize();

        Scene gameScene = new Scene(controller.getGamePane());
        gameScene.setOnMouseMoved(this::handleMouseMoved);

        primaryStage.setTitle("Brick Breaker Game");
        primaryStage.setScene(gameScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void handleMouseMoved(MouseEvent event) {
        controller.handleMouseMoved(event);
    }

    public static void main(String[] args) {
        launch(args);
    }
}