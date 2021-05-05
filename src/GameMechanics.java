import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.*;

public class GameMechanics {
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private GraphicsContext gc;
    private StackPane root;
    private Scene gameScene;
    private Color gameBgColor;
    private Random rnd;

    // User inputs
    private boolean playerMoveLeft;
    private boolean playerMoveRight;
    private boolean playerShoot;
    private boolean playerShootChecked;

    // Player
    static final Image PLAYER_IMG = new Image("Assets/Images/rocketclean64.png");
    private boolean playerInPosition;

    // Shots
    private double mouseX;
    final int MAX_BULLETS = 20;
    boolean gameOver = false;

    // Enemies
    private Enemy[] enemies;

    private Player player;
    private Enemy enemy;

    private LinkedList<Star> stars;
    private LinkedList<Shot> shots;

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
        gc.setFill(gameBgColor);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        playerMoveLeft = false;
        playerMoveRight = false;
        playerShoot = false;
        playerShootChecked = true;
        handleUserInputs();

        gameSetup();
    }

    /**
     * Starts running the game.
     */
    public void startGame() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(7), e -> run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Handles user inputs.
     */
    public void handleUserInputs() {
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) playerMoveLeft = true;
            if (e.getCode() == KeyCode.D) playerMoveRight = true;
            if (e.getCode() == KeyCode.SPACE) playerShoot = true;
        });
        gameScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) playerMoveLeft = false;
            if (e.getCode() == KeyCode.D) playerMoveRight = false;
            if (e.getCode() == KeyCode.SPACE) playerShoot = false;
        });
    }

    /**
     * Initialize everything that has to be initialized for the game to begin.
     */
    public void gameSetup() {
        // Player(int posX, int posY, int height, int width, int velocity, Image img, int health, int windowWidth)
        player = new Player(WINDOW_WIDTH / 2 - 32, WINDOW_HEIGHT - 128, 64, 64, 2, PLAYER_IMG, 100, WINDOW_WIDTH);

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


        stars = new LinkedList<>();
        shots = new LinkedList<>();
    }

    /**
     * Handles rendering and, subsequently, updates.
     */
    public void run() {
        gc.setFill(gameBgColor);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Stars
        renderStars();
        // Add new stars
        if (stars.size() <= 5 && rnd.nextFloat() < 0.3) {
            stars.add(createStar());
        } else if (stars.size() <= 30 && rnd.nextFloat() < 0.05) {
            stars.add(createStar());
        }

        // Shots
        renderShots();

        // Player
        if (playerInPosition) {
            // TODO handle inputs
            if (playerMoveLeft) player.moveLeft();
            if (playerMoveRight) player.moveRight();
            if (playerShoot) {
                Shot[] s = player.shoot();
                if (s != null) {
                    for (Shot shot : s) {
                        shots.add(shot);
                    }
                }
            }
            player.draw(gc);
        }


//        // Shots
//        LinkedList<Shot> deadShots = new LinkedList<>();
//        for (Shot shot : shots) {
//            if (isOutsideScreen(shot)) {
//                deadShots.add(shot);
//                continue;
//            }
//            shot.draw(gc);
//        }

//        // Shots
//        int score = 0; // when adding collision increase score for each kill
//        for (int j = shots.size() - 1; j >= 0 ; j--) {
//            Shot shot = shots.get(j);
//            if(shot.posY < 0 || shot.dead)  {
//                shots.remove(j);
//                continue;
//            }
//            shot.update();
//            shot.draw(gc, score);
//        }
    }

    private void renderStars() {
        LinkedList<Star> deadStars = new LinkedList<>();
        for (Star star : stars) {
            if (isOutsideScreen(star)) {
                deadStars.add(star);
            } else {
                star.draw(gc);
            }
        }
        // Remove dead stars
        for (Star deadStar : deadStars) {
            stars.remove(deadStar);
        }
    }

    private void renderShots() {
        LinkedList<Shot> deadShots = new LinkedList<>();
        for (Shot shot : shots) {
            if (isOutsideScreen(shot)) {
                deadShots.add(shot);
            } else {
                shot.draw(gc);
            }
        }
        // Remove dead shots
        for (Shot deadShot : deadShots) {
            shots.remove(deadShot);
        }
    }

    private boolean isOutsideScreen(Sprite who) {
        return who.posY > WINDOW_HEIGHT || who.posY < - who.height
                || who.posX < - who.width || who.posX > WINDOW_WIDTH;
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
        return new Star(posX, posY, height, width, velocity, color);
    }
}
