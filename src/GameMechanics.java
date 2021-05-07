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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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

    // Player
    static final Image PLAYER_IMG = new Image("Assets/Images/rocketclean64.png");
    private boolean playerInPosition;

    // Shots
    private double mouseX;
    final int MAX_BULLETS = 20;
    boolean gameOver = false;

    // Enemies
    private Enemy[] enemiesLoad;
    private int amountOfEnemies;
    private int enemiesLoaded;
    private double spawnProbability;

    private Player player;

    private LinkedList<Star> stars;
    private LinkedList<Star> deadStars;
    private LinkedList<Shot> playerShots;
    private LinkedList<Shot> playerDeadShots;
    private LinkedList<Shot> enemyShots;
    private LinkedList<Shot> enemyDeadShots;
    private LinkedList<Enemy> enemies;
    private LinkedList<Enemy> deadEnemies;
    private LinkedList<Enemy> explosions;
    private LinkedList<Enemy> deadExplosions;

    private Stopwatch stopwatch;
    private long fps;
    private LinkedList<Long> fpsArr;
    private int fpsSample;

    private double updateFrequency;

    public GameMechanics(int width, int height, Color color) {
        this.WINDOW_WIDTH = width;
        this.WINDOW_HEIGHT = height;
        this.gameBgColor = color;
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root = new StackPane(canvas);
        gameScene = new Scene(root);
        gc.setFill(gameBgColor);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        updateFrequency = 7;
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
    public void load() {
        this.rnd = new Random();
        playerInPosition = false;

        playerMoveLeft = false;
        playerMoveRight = false;
        playerShoot = false;
        handleUserInputs();

        gameSetup();
    }

    /**
     * Starts running the game.
     */
    public void startGame() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(updateFrequency), e -> run()));
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
     * Animate "the player" as if it's flying in from below
     */
    public void animatePlayerIn() {
        ImageView playerSub = new ImageView(PLAYER_IMG); // defaults to x = 0 and y = 0, cant change it (?)
        TranslateTransition playerIn = new TranslateTransition();
        playerIn.setDuration(Duration.seconds(1.5));
        playerIn.setFromY(550);
        playerIn.setToY(360);
        playerIn.setOnFinished(e -> {
            playerInPosition = true;
            root.getChildren().remove(playerSub);
        });
        playerIn.setNode(playerSub);
        playerIn.play();
        root.getChildren().add(playerSub);
    }

    /**
     * Initialize everything that has to be initialized for the game to begin.
     */
    public void gameSetup() {
        // Player(int posX, int posY, int height, int width, int velocity, Image img, int health, int windowWidth)
        player = new Player(WINDOW_WIDTH / 2 - PLAYER_IMG.getWidth()/2, WINDOW_HEIGHT - PLAYER_IMG.getHeight()*2, (int) PLAYER_IMG.getHeight(), (int) PLAYER_IMG.getWidth(), 2, PLAYER_IMG, 100, WINDOW_WIDTH, 1);

        // Load enemies according to level: //TODO
        amountOfEnemies = 20;
        enemiesLoad = new Enemy[amountOfEnemies];
        for (int i = 0; i < amountOfEnemies; i++) {
            // Read text file and assign values accordingly //TODO
            int n = 1 + rnd.nextInt(3);
            Image img = new Image("Assets/Images/enemyrocket" + n + ".png");
            double x = rnd.nextInt(WINDOW_WIDTH - (int) img.getWidth());
            double y = - (int) img.getHeight();
            double v = 1;
            int hp = 100;
            boolean s = true;
            boolean b = false;
            enemiesLoad[i] = new Enemy(x, y, v, img, hp, s, b, 0.003);
        }
        spawnProbability = 0.01;
        enemiesLoaded = 0;

        stars = new LinkedList<>();
        deadStars = new LinkedList<>();
        playerShots = new LinkedList<>();
        playerDeadShots = new LinkedList<>();
        enemyShots = new LinkedList<>();
        enemyDeadShots = new LinkedList<>();
        enemies = new LinkedList<>();
        deadEnemies = new LinkedList<>();
        explosions = new LinkedList<>();
        deadExplosions = new LinkedList<>();

        stopwatch = new Stopwatch();
        fpsArr = new LinkedList<>();
        fpsSample = 50;
    }

    /**
     * Handles rendering and, subsequently, updates.
     */
    public void run() {
        gc.setFill(gameBgColor);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        showPlayerScoreDuringGameplay();

        // Calculate FPS
        if (stopwatch.isRunning()) {
            long time = stopwatch.nanoseconds();
            fpsArr.add(time);
            if (fpsArr.size() == fpsSample) {
                long avg = 0;
                for (long num : fpsArr) {
                    avg += num;
                }
                avg = avg / fpsSample;
                System.out.println("FPS: " + 1000000000 / avg);
                fpsArr = new LinkedList<>();
            }
            stopwatch.reset();
        }
        stopwatch.start();

        // Stars
        renderStars();
        // Add new stars
        if (stars.size() <= 5 && rnd.nextFloat() < 0.3) {
            stars.add(createStar());
        } else if (stars.size() <= 30 && rnd.nextFloat() < 0.05) {
            stars.add(createStar());
        }

        // Player
        if (playerInPosition && !player.exploding) {
            // TODO handle inputs
            if (playerMoveLeft) player.moveLeft();
            if (playerMoveRight) player.moveRight();
            if (playerShoot) {
                Shot[] s = player.shoot();
                if (s != null) {
                    for (Shot shot : s) {
                        playerShots.add(shot);
                    }
                }
            }
            player.draw(gc);
        }

        // Shots
        renderShots();
        // Enemy shooting
        for (Enemy enemy : enemies) {
            if (enemy.ableToShoot) {
                if (rnd.nextDouble() < enemy.shootingProbability) {
                    Shot[] s = enemy.shoot();
                    if (s != null) {
                        for (Shot shot : s) {
                            enemyShots.add(shot);
                        }
                    }
                }
            }

            // Check player-enemy collisions
            if (player.hasCollided(enemy) && !player.exploding) {
                player.explode();
                enemy.explode();
                explosions.add(enemy);
                deadEnemies.add(enemy);
                continue;
            }

            // Check if player bullet hits enemy
            for (Shot shot : playerShots) {
                if (shot.hasCollided(enemy)) {
                    enemy.explode();
                    explosions.add(enemy);
                    deadEnemies.add(enemy);
                    playerDeadShots.add(shot);
                    player.updateScore();
                }
            }
        }

        // Enemies
        renderEnemies();
        // Spawn next Enemy randomly if not all spawned already and if player is alive
        if (enemiesLoaded < amountOfEnemies && playerInPosition && !player.exploding) {
            if (rnd.nextDouble() < spawnProbability || enemiesLoaded == 0) {
                Enemy currentEnemy = enemiesLoad[enemiesLoaded];
                // TODO clever spawning? this is just a lazy fix
                boolean flag = false;
                for (Enemy spawnedEnemy : enemies) {
                    if (spawnedEnemy.hasCollided(currentEnemy)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    enemies.add(enemiesLoad[enemiesLoaded]);
                    enemiesLoaded++;
                }
            }
        }

        // Check if enemy bullet hits player
        if (!player.exploding) {
            for (Shot shot : enemyShots) {
                if (player.hasCollided(shot)) {
                    player.hit(shot);
                    enemyDeadShots.add(shot);
                }
            }
        }

        // Render explosions
        renderExplosions();

        // Remove all dead
        removeDead();

        // TODO: Check if player is dead; game over
        if (player.dead) {
            gc.setFont(Font.font(30));
            gc.setFill(Color.RED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("The Enemy Won \n Your Score is: " + player.getScore() + " \n\n Click here to play again",
                    WINDOW_WIDTH/2, WINDOW_HEIGHT/3);
        }

        // TODO: Check if all enemies are dead; game won
        if (allEnemiesLoadedAndAllAreDead()) {
            gc.setFont(Font.font(30));
            gc.setFill(Color.RED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("The Enemies Have Been Destroyed!" + " \n\n Click here to play the next level",
                    WINDOW_WIDTH/2, WINDOW_HEIGHT/3);
        }

        // TODO check if player score is worthy of an achievement


    }

    private void showPlayerScoreDuringGameplay() {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font(20));
        gc.setFill(Color.YELLOW);
        gc.fillText("Score: " + player.getScore(), 10,  20);
    }

    private boolean allEnemiesLoadedAndAllAreDead() {
        return enemiesLoaded == enemiesLoad.length && enemies.size() == 0;
    }

    private void renderStars() {
        for (Star star : stars) {
            if (isOutsideScreen(star)) {
                deadStars.add(star);
            } else {
                star.draw(gc);
            }
        }
    }

    private void renderShots() {
        for (Shot shot : playerShots) {
            if (isOutsideScreen(shot)) {
                playerDeadShots.add(shot);
            } else {
                shot.draw(gc);
            }
        }
        for (Shot shot : enemyShots) {
            if (isOutsideScreen(shot)) {
                enemyDeadShots.add(shot);
            } else {
                shot.draw(gc);
            }
        }
    }

    private void renderEnemies() {
        for (Enemy enemy : enemies) {
            if (isOutsideScreen(enemy)) {
                deadEnemies.add(enemy);
            } else {
                enemy.draw(gc);
            }
        }
    }

    private void renderExplosions() {
        for (Enemy explosion : explosions) {
            if (explosion.dead) {
                deadExplosions.add(explosion);
            } else {
                explosion.draw(gc);
            }
        }
        if (player.exploding) {
            player.draw(gc);
        }
    }

    private void removeDead() {
        // Remove dead stars
        for (Star deadStar : deadStars) {
            stars.remove(deadStar);
        }
        // Remove dead shots
        for (Shot deadShot : playerDeadShots) {
            playerShots.remove(deadShot);
        }
        // Remove dead shots
        for (Shot deadShot : enemyDeadShots) {
            enemyShots.remove(deadShot);
        }
        // Remove dead enemies
        for (Enemy deadEnemy : deadEnemies) {
            enemies.remove(deadEnemy);
        }
        // Remove dead explosions
        for (Enemy deadExplosion : deadExplosions) {
            explosions.remove(deadExplosion);
        }
        deadStars.clear();
        playerDeadShots.clear();
        deadEnemies.clear();
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
