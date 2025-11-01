package com.example.brickshootergame;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.ArrayList;

public class GameObjects {
    private static final double BALL_RADIUS = 7.5;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 12;
    private static final int BRICK_ROWS = 7;
    private static final int BRICK_COLS = 10;
    private static final double BRICK_WIDTH = 60;
    private static final double BRICK_HEIGHT = 20;
    private static final double BRICK_SPACING = 7.5;

    private final AnchorPane gamePane;
    private final Circle ball;
    private final Rectangle paddle;
    private final Rectangle bottomZone;
    private final Button startButton;
    private final Text scoreText;
    private final Text livesText;

    private final ArrayList<Rectangle> bricks = new ArrayList<>();
    private final int sceneWidth;
    private final int sceneHeight;

    public GameObjects(int sceneWidth, int sceneHeight) {
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;

        gamePane = new AnchorPane();
        gamePane.setPrefSize(sceneWidth, sceneHeight);
        gamePane.setStyle("-fx-background-color: #3aa6ff;");

        ball = new Circle(BALL_RADIUS, Color.WHITE);
        ball.setStroke(Color.BLACK);
        ball.setStrokeWidth(1.5);
        ball.setLayoutX(sceneWidth / 2.0);
        ball.setLayoutY(sceneHeight - 50 - BALL_RADIUS - 1);
        ball.setVisible(false);

        paddle = new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT, Color.BLACK);
        paddle.setArcHeight(5);
        paddle.setArcWidth(5);
        paddle.setLayoutX((sceneWidth - PADDLE_WIDTH) / 2.0);
        paddle.setLayoutY(sceneHeight - 50);
        paddle.setVisible(false);

        bottomZone = new Rectangle(sceneWidth, 5, Color.TRANSPARENT);
        bottomZone.setLayoutY(sceneHeight - 5);

        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreText.setFill(Color.WHITE);
        scoreText.setLayoutX(20);
        scoreText.setLayoutY(30);

        livesText = new Text();
        livesText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        livesText.setFill(Color.RED);
        livesText.setLayoutX(sceneWidth - 100);
        livesText.setLayoutY(30);

        startButton = new Button("START GAME");
        startButton.setLayoutX(sceneWidth / 2.0 - 112);
        startButton.setLayoutY(sceneHeight / 2.0 - 55);
        startButton.setPrefSize(225, 111);
        startButton.setFont(new Font("Arial Bold", 28));
        startButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 10;");

        gamePane.getChildren().addAll(bottomZone, ball, paddle, scoreText, livesText, startButton);
    }

    public void createBricks() {
        clearBricks();
        double startX = (sceneWidth - (BRICK_COLS * (BRICK_WIDTH + BRICK_SPACING))) / 2;
        double startY = 50;

        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PURPLE};

        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                Rectangle brick = new Rectangle(
                        startX + col * (BRICK_WIDTH + BRICK_SPACING),
                        startY + row * (BRICK_HEIGHT + BRICK_SPACING),
                        BRICK_WIDTH, BRICK_HEIGHT
                );
                brick.setFill(colors[row % colors.length]);
                brick.setStroke(Color.BLACK);
                brick.setStrokeWidth(1);
                brick.setArcHeight(5);
                brick.setArcWidth(5);
                bricks.add(brick);
                gamePane.getChildren().add(brick);
            }
        }
    }

    public void clearBricks() {
        gamePane.getChildren().removeAll(bricks);
        bricks.clear();
    }

    public void updateScoreText(int score) {
        scoreText.setText("Score: " + score);
    }

    public void updateLivesText(int lives) {
        livesText.setText("♥ ".repeat(Math.max(0, lives)).trim());
    }

    public AnchorPane getGamePane() { return gamePane; }
    public Circle getBall() { return ball; }
    public Rectangle getPaddle() { return paddle; }
    public Rectangle getBottomZone() { return bottomZone; }
    public Button getStartButton() { return startButton; }
    public ArrayList<Rectangle> getBricks() { return bricks; }
    public int getSceneWidth() { return sceneWidth; }
    public int getSceneHeight() { return sceneHeight; }
    public static double getBallRadius() { return BALL_RADIUS; }
    public static int getPaddleWidth() { return PADDLE_WIDTH; }
}