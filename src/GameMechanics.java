import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameMechanics {
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private GraphicsContext gc;
    private StackPane root;
    private Scene gameScene;
    private Color gameBgColor;
    private Random rnd;

    // Player
    static final Image PLAYER_IMG = new Image("Assets/Images/rocketclean64.png");
    private boolean playerInPosition;

    // Shots
    private double mouseX;
    List<Shot> shots = new ArrayList<>();
    final int MAX_BULLETS = 20;
    boolean gameOver = false;

    private Player player;
    private Enemy enemy;
    private ArrayList<Star> stars;

    public GameMechanics(int width, int height, Color color) {
        this.WINDOW_WIDTH = width;
        this.WINDOW_HEIGHT = height;
        this.gameBgColor = color;
        this.rnd = new Random();
        playerInPosition = false;
        game();
    }

    /**
     * Get the game Scene.
     * @return The game Scene.
     */
    public Scene getGameScene() {
        return gameScene;
    }

    /**
     * Start the game.
     */
    public void game() {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root = new StackPane(canvas);
        gameScene = new Scene(root);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(7), e -> run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        handleKeyboardMovement();
        handleMouseMovement(canvas);

        gameSetup();
    }

    // When pressing one key (e.g. move to right) one cannot press others (e.g. shoot)
    // TODO: See tutorial on how to fix these with booleans
    private void handleKeyboardMovement() {
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) {
                player.moveLeft();
            } else if (e.getCode() == KeyCode.D) {
                player.moveRight();
            }
            else if (e.getCode() == KeyCode.SPACE) {
                // TODO: Fix bug. Cannot shoot when size > max bullets
                if(shots.size() < MAX_BULLETS) {
                    shots.add(player.shoot());
                } else {
                    // set shot.getToRemove() = true;
                }
            }
        });
    }

    private void handleMouseMovement(Canvas canvas) {
        canvas.setCursor(Cursor.MOVE);
        canvas.setOnMouseMoved(e -> mouseX = e.getX());
        canvas.setOnMouseClicked(e -> {
            if(shots.size() < MAX_BULLETS) {
                shots.add(player.shoot());
            }
        });
    }

    /**
     * Initialize everything that has to be initialized for the game to begin.
     */
    public void gameSetup() {
        // Player(int posX, int posY, int height, int width, int velocity, Image img, int health, int windowWidth)
        player = new Player(WINDOW_WIDTH / 2 - 32, WINDOW_HEIGHT - 128, 64, 64, 10, PLAYER_IMG, 100, WINDOW_WIDTH);

        // Animate "the player" as if it's flying in from below
        ImageView playerSub = new ImageView(PLAYER_IMG);
        playerSub.setX(WINDOW_WIDTH / 2 - 32);
        playerSub.setY(100);
        root.getChildren().add(playerSub);
        TranslateTransition playerIn = new TranslateTransition();
        playerIn.setDuration(Duration.seconds(1.5));
        playerIn.setFromY(500);
        playerIn.setToY(334);
        playerIn.setOnFinished(e -> {
            playerInPosition = true;
            root.getChildren().remove(playerSub);
        });
        playerIn.setNode(playerSub);
        playerIn.play();

        // TODO: Setup enemies
        // Enemy(int posX, int posY, int height, int width, int velocity, Image img, int health, boolean ableToShoot, int windowHeight)
        // enemy = new Enemy(WINDOW_WIDTH / 2 - 16, WINDOW_HEIGHT, 64, 64, 10, ENEMY1_IMG, 100, ableToShoot, WINDOW_HEIGHT);


        stars = new ArrayList<>();
    }

    /**
     * Handles rendering and, subsequently, updates.
     */
    public void run() {
        gc.setFill(gameBgColor);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

//        gc.setFill(Color.RED);
//        gc.fillOval(100, 100, 200, 100);
//        gc.fillRect(100, 100, 100, 100);
//        gc.fillRect(100, 400, 1, 1);
//        gc.fillRect(100, 500, 5, 5);
//        gc.fillRect(100, 600, 10, 10);

        // Stars
        ArrayList<Star> deadStars = new ArrayList<>();
        Iterator<Star> i = stars.iterator();
        while (i.hasNext()) {
            Star star = i.next();
            if (star.dead) {
                deadStars.add(star);
            } else if (star.posY >= WINDOW_HEIGHT - star.height) {
                star.dead = true;
            } else {
                star.draw(gc);
            }
        }
        // Remove dead stars
        for (Star deadStar : deadStars) {
            stars.remove(deadStar);
        }

//        for (Star star : stars) {
//            if (star.dead) {
//                stars.remove(star);
//            } else if (star.posY >= WINDOW_HEIGHT - star.height) {
//                star.dead = true;
//            } else {
//                star.draw(gc);
//            }
//        }

        if (stars.size() <= 5 && rnd.nextFloat() < 0.3) {
            stars.add(createStar());
        } else if (stars.size() <= 30 && rnd.nextFloat() < 0.05) {
            stars.add(createStar());
        }

        // Player
        if (playerInPosition) {
            // TODO handle inputs
            player.draw(gc);
        }

        // Enemies
//        if (enemyInPosition) {
//            // TODO handle inputs
//            enemy.draw(gc);
//        }

        int score = 0; // when adding collision increase score for each kill
        for (int j = shots.size() - 1; j >=0 ; j--) {
            Shot shot = shots.get(j);
            if(shot.posY < 0 || shot.toRemove)  {
                shots.remove(j);
                continue;
            }
            shot.update();
            shot.draw(gc, score);
        }
    }

    /**
     * Generates a star with random parameters to allow for unique distributions with certain distance dependencies.
     * @return A randomly generated star, using set bounds.
     */
    public Star createStar() {
        double distance = rnd.nextDouble();
        int height = 4 + rnd.nextInt(2);
        int width = height;
        int posX = rnd.nextInt(WINDOW_WIDTH - width + 1);
        double posY = -width;
        double velocity = .6 + distance;
        Color color = Color.grayRgb(20 + 1 + (int) (distance * 100));       // By principle of proximity = stronger light only
        // Alternative color methods:
//        Color color = Color.grayRgb(40 + r.nextInt(1 + (int) (distance * 160)));          // Allows more unique light strength which is amplified by proximity
//        Color color = Color.grayRgb(40 + (int) (distance * 100) / 2 + r.nextInt(1 + (int) (distance * 100) / 2));     // A mix of the two previously mentioned methods
        return new Star(posX, posY, height, width, velocity, color);
    }
}
