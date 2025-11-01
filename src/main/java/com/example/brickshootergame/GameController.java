package com.example.brickshootergame;

import javafx.animation.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Iterator;

public class GameController {
    private static final double BALL_SPEED = 3;
    private static final int INITIAL_LIVES = 3;
    private static final int POINTS_PER_BRICK = 5;

    private final GameObjects gameObjects;
    private final ArrayList<Timeline> particleTimelines = new ArrayList<>();

    private double deltaX = 2;
    private double deltaY = -3;
    private double mouseX;
    private int score = 0;
    private int lives = INITIAL_LIVES;
    private boolean gameRunning = false;
    private boolean ballLaunched = false;

    private Timeline timeline;

    public GameController(int sceneWidth, int sceneHeight) {
        gameObjects = new GameObjects(sceneWidth, sceneHeight);
        mouseX = sceneWidth / 2.0;
    }

    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.millis(10), e -> gameLoop()));
        timeline.setCycleCount(Animation.INDEFINITE);
        gameObjects.getStartButton().setOnAction(e -> {
            gameObjects.getStartButton().setVisible(false);
            startGame();
        });
    }

    public void handleMouseMoved(MouseEvent event) {
        mouseX = Math.max(gameObjects.getPaddle().getWidth() / 2,
                Math.min(event.getSceneX(), gameObjects.getSceneWidth() - gameObjects.getPaddle().getWidth() / 2));
    }

    private void gameLoop() {
        if (!gameRunning) return;
        movePaddle();
        if (ballLaunched) {
            normalizeSpeed();
            Circle ball = gameObjects.getBall();
            ball.setLayoutX(ball.getLayoutX() + deltaX);
            ball.setLayoutY(ball.getLayoutY() + deltaY);
            checkCollisions();
        } else followPaddle();
    }

    private void normalizeSpeed() {
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        deltaX = (deltaX / speed) * BALL_SPEED;
        deltaY = (deltaY / speed) * BALL_SPEED;
    }

    private void startGame() {
        gameRunning = true;
        ballLaunched = false;
        score = 0;
        lives = INITIAL_LIVES;
        gameObjects.getBall().setVisible(true);
        gameObjects.getPaddle().setVisible(true);
        gameObjects.updateScoreText(score);
        gameObjects.updateLivesText(lives);
        gameObjects.createBricks();
        resetBall();
        cleanupParticles();
        gameObjects.getGamePane().setOnMouseClicked(e -> {
            if (!ballLaunched) ballLaunched = true;
        });
        timeline.play();
    }

    private void resetBall() {
        ballLaunched = false;
        Rectangle paddle = gameObjects.getPaddle();
        Circle ball = gameObjects.getBall();
        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
        ball.setLayoutY(paddle.getLayoutY() - GameObjects.getBallRadius() - 1);
        deltaX = 2;
        deltaY = -3;
    }

    private void movePaddle() {
        Rectangle paddle = gameObjects.getPaddle();
        double newX = Math.max(0, Math.min(mouseX - paddle.getWidth() / 2,
                gameObjects.getSceneWidth() - paddle.getWidth()));
        paddle.setLayoutX(newX);
    }

    private void followPaddle() {
        Rectangle paddle = gameObjects.getPaddle();
        Circle ball = gameObjects.getBall();
        ball.setLayoutX(paddle.getLayoutX() + paddle.getWidth() / 2);
        ball.setLayoutY(paddle.getLayoutY() - GameObjects.getBallRadius() - 1);
    }

    private void checkCollisions() {
        checkWalls();
        checkPaddle();
        checkBricks();
        checkBottom();
    }

    private void checkWalls() {
        Circle ball = gameObjects.getBall();
        double x = ball.getLayoutX(), y = ball.getLayoutY(), r = GameObjects.getBallRadius();
        if (x >= gameObjects.getSceneWidth() - r) { deltaX = -Math.abs(deltaX); ball.setLayoutX(gameObjects.getSceneWidth() - r); }
        else if (x <= r) { deltaX = Math.abs(deltaX); ball.setLayoutX(r); }
        if (y <= r) { deltaY = Math.abs(deltaY); ball.setLayoutY(r); }
    }

    private void checkPaddle() {
        Circle ball = gameObjects.getBall();
        Rectangle paddle = gameObjects.getPaddle();
        if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent()) && deltaY > 0) {
            deltaY = -Math.abs(deltaY);
            double hit = (ball.getLayoutX() - (paddle.getLayoutX() + paddle.getWidth() / 2)) / (paddle.getWidth() / 2);
            deltaX = hit * BALL_SPEED * 0.7;
            ball.setLayoutY(paddle.getLayoutY() - GameObjects.getBallRadius() - 1);
        }
    }

    private void checkBricks() {
        ArrayList<Rectangle> bricks = gameObjects.getBricks();
        if (bricks.isEmpty()) { showVictory(); return; }
        Circle ball = gameObjects.getBall();
        Iterator<Rectangle> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            Rectangle brick = iterator.next();
            if (ball.getBoundsInParent().intersects(brick.getBoundsInParent())) {
                handleBrickCollision(brick);
                score += POINTS_PER_BRICK;
                gameObjects.updateScoreText(score);
                animateBrickDestruction(brick);
                iterator.remove();
                break;
            }
        }
    }

    private void handleBrickCollision(Rectangle brick) {
        Circle ball = gameObjects.getBall();
        double bx = ball.getLayoutX(), by = ball.getLayoutY(), br = GameObjects.getBallRadius();
        double overlapLeft = (bx + br) - brick.getX();
        double overlapRight = (brick.getX() + brick.getWidth()) - (bx - br);
        double overlapTop = (by + br) - brick.getY();
        double overlapBottom = (brick.getY() + brick.getHeight()) - (by - br);
        double min = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
        if (min == overlapLeft || min == overlapRight) deltaX *= -1;
        else deltaY *= -1;
    }

    private void checkBottom() {
        if (gameObjects.getBall().getBoundsInParent().intersects(gameObjects.getBottomZone().getBoundsInParent()))
            loseLife();
    }

    private void loseLife() {
        lives--;
        gameObjects.updateLivesText(lives);
        if (lives <= 0) gameOver();
        else resetBall();
    }

    private void gameOver() {
        stopGame();
        showEndScreen("GAME OVER!", Color.RED, "RESTART");
    }

    private void showVictory() {
        stopGame();
        showEndScreen("YOU WIN!", Color.LIMEGREEN, "PLAY AGAIN");
    }

    private void stopGame() {
        gameRunning = false;
        ballLaunched = false;
        timeline.stop();
        gameObjects.clearBricks();
        cleanupParticles();
        resetBall();
        gameObjects.getBall().setVisible(false);
        gameObjects.getPaddle().setVisible(false);
        gameObjects.getGamePane().setOnMouseClicked(null);
    }

    private void showEndScreen(String message, Color color, String buttonText) {
        AnchorPane pane = gameObjects.getGamePane();
        Text msg = new Text(message);
        msg.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        msg.setFill(color);
        msg.setLayoutX((gameObjects.getSceneWidth() - msg.getLayoutBounds().getWidth()) / 2);
        msg.setLayoutY(gameObjects.getSceneHeight() / 2.0 - 30);

        Text scoreText = new Text("Final Score: " + score);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreText.setFill(Color.WHITE);
        scoreText.setLayoutX((gameObjects.getSceneWidth() - scoreText.getLayoutBounds().getWidth()) / 2);
        scoreText.setLayoutY(gameObjects.getSceneHeight() / 2.0 + 20);

        pane.getChildren().addAll(msg, scoreText);
        PauseTransition pause = new PauseTransition(Duration.seconds(4));
        pause.setOnFinished(e -> {
            pane.getChildren().removeAll(msg, scoreText);
            gameObjects.getStartButton().setText(buttonText);
            gameObjects.getStartButton().setVisible(true);
        });
        pause.play();
    }

    private void animateBrickDestruction(Rectangle brick) {
        double cx = brick.getX() + brick.getWidth() / 2, cy = brick.getY() + brick.getHeight() / 2;
        Color color = (Color) brick.getFill();
        gameObjects.getGamePane().getChildren().remove(brick);
        for (int i = 0; i < 8; i++) createParticle(cx, cy, color, i);
    }

    private void createParticle(double x, double y, Color color, int index) {
        Rectangle p = new Rectangle(5, 5, color);
        gameObjects.getGamePane().getChildren().add(p);
        double angle = (Math.PI * 2 * index) / 8;
        double vx = Math.cos(angle) * 3, vy = Math.sin(angle) * 3;
        final double[] px = {x}, py = {y}, op = {1};
        Timeline t = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            px[0] += vx; py[0] += vy + 0.2; op[0] -= 0.03;
            p.setX(px[0]); p.setY(py[0]); p.setOpacity(op[0]);
            if (op[0] <= 0) gameObjects.getGamePane().getChildren().remove(p);
        }));
        t.setCycleCount(35);
        t.setOnFinished(e -> particleTimelines.remove(t));
        t.play();
        particleTimelines.add(t);
    }

    private void cleanupParticles() {
        for (Timeline t : particleTimelines) t.stop();
        particleTimelines.clear();
        gameObjects.getGamePane().getChildren().removeIf(n -> n instanceof Rectangle r && r.getWidth() == 5 && r.getHeight() == 5);
    }

    public AnchorPane getGamePane() { return gameObjects.getGamePane(); }
}