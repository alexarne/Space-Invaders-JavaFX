import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class GameMechanics {
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private GraphicsContext gc;
    private Pane root;
    private Scene gameScene;
    private Random rnd;
    private Stage window;

    // User inputs
    private boolean playerMoveLeft;
    private boolean playerMoveRight;
    private boolean playerShoot;
    private boolean playerReload;
    private boolean playerESC;

    // Player
    static final Image PLAYER_IMG = new Image("Assets/Images/rocketclean64.png");
    static final Image HP_IMG = new Image("Assets/Images/hp_icon.png");
    static final Image AMMO_IMG = new Image("Assets/Images/ammo_icon.png");
    private boolean playerInPosition;
    private Rectangle hudAnimate;

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
    private Color gameBgColor;

    private Color hpColor;
    private Color ammoColor;
    private Color gameoverColor;
    private Color gameoverColorHover;

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
    private LinkedList<Long> fpsArr;
    private int fpsSample;

    private boolean gameoverDisplayed;
    private Text buttonQuit;
    private Text buttonRetry;
    private Text buttonNext;

    private Timeline timeline;

    private double updateFrequency;

    public GameMechanics(int width, int height, Stage window, Color color, Random rnd) {
        this.WINDOW_WIDTH = width;
        this.WINDOW_HEIGHT = height;
        this.gameBgColor = color;
        this.gameoverDisplayed = false;
        this.window = window;
        this.rnd = rnd;
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root = new Pane(canvas);
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
        this.hpColor = Color.rgb(64, 221, 61);
        this.ammoColor = Color.rgb(217, 151, 52);
        this.gameoverColor = Color.rgb(212, 72, 51);
        this.gameoverColorHover = Color.rgb(255, 135, 117);

        playerInPosition = false;

        playerMoveLeft = false;
        playerMoveRight = false;
        playerShoot = false;
        playerESC = false;
        handleUserInputs();

        prepareButtons();
        gameSetup();
    }

    private void prepareButtons() {
        Main main = new Main();
        Duration duration = Duration.millis(300);
        Interpolator interp = Interpolator.EASE_OUT;

        buttonQuit = makeGameoverButton("Quit");
        buttonQuit.setOnMouseReleased(e -> {
            main.animateButton(buttonQuit, true, duration, interp);
            main.animateButton(buttonRetry, false, duration, interp);
            main.animateButton(buttonNext, false, duration, interp);

            Rectangle r = new Rectangle();
            r.setFill(Color.WHITE);
            r.setWidth(WINDOW_WIDTH);
            r.setHeight(WINDOW_HEIGHT);
            r.setOpacity(0);
            FadeTransition fade = new FadeTransition();
            fade.setDuration(duration);
            fade.setToValue(1);
            fade.setNode(r);
            fade.setInterpolator(interp);
            fade.setOnFinished(f -> {
                Scene mainScene = main.getMainScene();
                window.setScene(mainScene);
                timeline.stop();
            });
            root.getChildren().add(r);
            fade.play();
            main.mainMenu(window);
        });
        buttonRetry = makeGameoverButton("Try Again");
        buttonRetry.setOnMouseReleased(e -> {
            main.animateButton(buttonRetry, true, duration, interp);
            main.animateButton(buttonRetry, false, duration, interp);
            main.animateButton(buttonNext, false, duration, interp);

            GameMechanics gameMechanics = new GameMechanics(WINDOW_WIDTH, WINDOW_HEIGHT, window, gameBgColor, rnd);
            Rectangle r = new Rectangle();
            r.setFill(gameBgColor);
            r.setWidth(WINDOW_WIDTH);
            r.setHeight(WINDOW_HEIGHT);
            r.setOpacity(0);
            FadeTransition fade = new FadeTransition();
            fade.setDuration(duration);
            fade.setToValue(1);
            fade.setNode(r);
            fade.setInterpolator(interp);
            fade.setOnFinished(f -> {
                gameMechanics.load();
                Scene game = gameMechanics.getGameScene();
                window.setScene(game);
                gameMechanics.startGame();
            });
            root.getChildren().add(r);
            fade.play();
            gameMechanics.animatePlayerIn();
        });
        buttonNext = makeGameoverButton("Next Level");

    }

    private Text makeGameoverButton(String s) {
        Text button = new Text(s);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setFont(Font.font("Sitka Small", 24));
        button.setFill(gameoverColor);
        button.setX(WINDOW_WIDTH / 2 - button.getLayoutBounds().getWidth() / 2);
        button.setOnMouseEntered(e -> button.setFill(gameoverColorHover));
        button.setOnMouseExited(e -> button.setFill(gameoverColor));
        return button;
    }

    /**
     * Starts running the game.
     */
    public void startGame() {
        timeline = new Timeline(new KeyFrame(Duration.millis(updateFrequency), e -> run()));
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
            if (e.getCode() == KeyCode.R) playerReload = true;
            if (e.getCode() == KeyCode.ESCAPE) {
                if (playerInPosition && !gameoverDisplayed) toggleESC();
            }
        });
        gameScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) playerMoveLeft = false;
            if (e.getCode() == KeyCode.D) playerMoveRight = false;
            if (e.getCode() == KeyCode.SPACE) playerShoot = false;
            if (e.getCode() == KeyCode.R) playerReload = false;
        });
    }

    private void toggleESC() {
        if (playerESC) {
            playerESC = false;
            timeline.play();
        } else {
            playerESC = true;
            timeline.pause();
        }
    }

    /**
     * Animate "the player" as if it's flying in from below
     */
    public void animatePlayerIn() {
        Duration duration = Duration.seconds(1.5);

        hudAnimate = new Rectangle();
        double w = WINDOW_WIDTH-20;
        int h = 100;
        hudAnimate.setFill(gameBgColor);
        hudAnimate.setWidth(w);
        hudAnimate.setHeight(h);
        hudAnimate.setX(WINDOW_WIDTH/2 - w/2);
        hudAnimate.setY(WINDOW_HEIGHT - h);
        ScaleTransition s = new ScaleTransition();
        s.setToX(1-304/w);
        s.setNode(hudAnimate);
        s.setDuration(duration.divide(1.5));
        s.setOnFinished(e -> root.getChildren().remove(hudAnimate));

        ImageView playerSub = new ImageView(PLAYER_IMG);
        playerSub.setX(WINDOW_WIDTH / 2 - PLAYER_IMG.getWidth()/2);
        TranslateTransition playerIn = new TranslateTransition();
        playerIn.setDuration(duration);
        playerIn.setFromY(WINDOW_HEIGHT+100);
        playerIn.setToY(WINDOW_HEIGHT - PLAYER_IMG.getHeight()*2);
        playerIn.setOnFinished(e -> {
            playerInPosition = true;
            root.getChildren().remove(playerSub);
        });
        playerIn.setNode(playerSub);
        playerIn.play();

        PauseTransition p = new PauseTransition();
        p.setDuration(duration.divide(5));
        p.setOnFinished(e -> s.play());
        p.play();

        root.getChildren().add(hudAnimate);
        root.getChildren().add(playerSub);
    }

    /**
     * Initialize everything that has to be initialized for the game to begin.
     */
    public void gameSetup() {
        // Player(int posX, int posY, int height, int width, int velocity, Image img, int health, int windowWidth)
        int sA = 1;
        int dP = 40;
        player = new Player(WINDOW_WIDTH / 2 - PLAYER_IMG.getWidth()/2, WINDOW_HEIGHT - PLAYER_IMG.getHeight()*2, (int) PLAYER_IMG.getHeight(), (int) PLAYER_IMG.getWidth(), 2, PLAYER_IMG, 100, WINDOW_WIDTH, sA, dP);

        // Load enemies according to level: //TODO
        LevelLoader levelLoader = new LevelLoader(WINDOW_WIDTH, gameBgColor, rnd);
        enemiesLoad = levelLoader.getEnemies(2);
        amountOfEnemies = levelLoader.getAmountOfEnemies();
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
        showAmountOfEnemiesLeft(); // see the methods comment below

        calculateFPS();

        // Stars
        renderStars();
        addNewStars();

        // Player
        handlePlayerMovementAndShooting();

        // Shots
        renderShots();

        // Enemy shooting
        for (Enemy enemy : enemies) { // TODO: Too much happening, hard to clean the code. Should be three methods.
            handleEnemyShooting(enemy);
            if (playerAndEnemyCollide(enemy)) continue;
            checkIfPlayerBulletHitsEnemy(enemy);
        }

        // Enemies
        renderEnemies();
        handleEnemySpawning();
        checkIfEnemyBulletHitsPLayer();

        renderExplosions();
        removeDead();

        // Game state
        handleGameOverIfPlayerDead();
        handleGameWonIfAllEnemiesDead();
      
        renderHUD();

        // TODO check if player score is worthy of an achievement
    }

    private void showPlayerScoreDuringGameplay() {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font(20));
        gc.setFill(Color.YELLOW);
        gc.fillText("Score: " + player.getScore(), 10,  20);
    }

    // TODO: Implement idea or discard it.
    private void showAmountOfEnemiesLeft() {
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(Font.font(20));
        gc.setFill(Color.YELLOW);
        int amountLeft = 0;
        gc.fillText("Enemies left: " + amountLeft, 500,  20);
    }

    private void calculateFPS() {
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
    }

    private void addNewStars() {
        if (stars.size() <= 5 && rnd.nextFloat() < 0.3) {
            stars.add(createStar());
        } else if (stars.size() <= 30 && rnd.nextFloat() < 0.05) {
            stars.add(createStar());
        }
    }

    private void handlePlayerMovementAndShooting() {
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
            if (playerReload && !player.reloading) {
                player.reload();
            }
            player.draw(gc);
        }
    }

    private void handleEnemyShooting(Enemy enemy) {
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
    }

    private boolean playerAndEnemyCollide(Enemy enemy) {
        if (player.hasCollided(enemy) && !player.exploding) {
            player.explode();
            enemy.explode();
            explosions.add(enemy);
            deadEnemies.add(enemy);
            return true;
        }
        return false;
    }

    private void checkIfPlayerBulletHitsEnemy(Enemy enemy) {
        for (Shot shot : playerShots) {
            if (shot.hasCollided(enemy)) {
                enemy.hit(shot);
                playerDeadShots.add(shot);
                if (enemy.healthCurrent == 0) {
                    explosions.add(enemy);
                    deadEnemies.add(enemy);
                    player.updateScore();
                }
            }
        }
    }

    private void handleEnemySpawning() {
        if (thereAreEnemiesToLoadAndPlayerIsAlive()) {
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
    }

    private boolean thereAreEnemiesToLoadAndPlayerIsAlive() {
        return enemiesLoaded < amountOfEnemies && playerInPosition && !player.exploding;
    }

    private void checkIfEnemyBulletHitsPLayer() {
        if (!player.exploding) {
            for (Shot shot : enemyShots) {
                if (player.hasCollided(shot)) {
                    player.hit(shot);
                    enemyDeadShots.add(shot);
                }
            }
        }
    }

    private void handleGameOverIfPlayerDead() {
        if (player.dead) {
            gc.setFont(Font.font("Sitka Small", 40));
            gc.setFill(gameoverColor);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Game Over", WINDOW_WIDTH/2, WINDOW_HEIGHT/3);
            gc.setFont(Font.font("Sitka Small", 24));
            gc.fillText("Score: " + player.getScore(), WINDOW_WIDTH/2, WINDOW_HEIGHT/3 + 40);

            if (!gameoverDisplayed) {
                buttonRetry.setY(WINDOW_HEIGHT/3 + 120);
                buttonQuit.setY(WINDOW_HEIGHT/3 + 160);
                root.getChildren().addAll(buttonRetry, buttonQuit);
                gameoverDisplayed = true;
            }
        }
    }

    private void handleGameWonIfAllEnemiesDead() {
        // TODO: Make it so that the text "Click here" leads to a new level.
        if (allEnemiesLoadedAndAllAreDead() && !player.exploding) {
            gc.setFont(Font.font("Sitka Small", 40));
            gc.setFill(gameoverColor);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Level Completed",
                    WINDOW_WIDTH/2, WINDOW_HEIGHT/3);
            gc.setFont(Font.font("Sitka Small", 24));
            gc.fillText("Score: " + player.getScore(), WINDOW_WIDTH/2, WINDOW_HEIGHT/3 + 40);

            if (!gameoverDisplayed) {
                buttonNext.setY(WINDOW_HEIGHT/3 + 120);
                buttonRetry.setY(WINDOW_HEIGHT/3 + 160);
                buttonQuit.setY(WINDOW_HEIGHT/3 + 200);
                root.getChildren().addAll(buttonNext, buttonRetry, buttonQuit);
                gameoverDisplayed = true;
            }
        }
    }

    private boolean allEnemiesLoadedAndAllAreDead() {
        return enemiesLoaded == enemiesLoad.length && enemies.size() == 0 && explosions.size() == 0;
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
        removeDeadStars();
        removePlayerDeadShots(playerDeadShots, playerShots);
        removeEnemyDeadShots(enemyDeadShots, enemyShots);
        removeDeadEnemies(deadEnemies, enemies);
        removeDeadExplosions(deadExplosions, explosions);

        deadStars.clear();
        playerDeadShots.clear();
        deadEnemies.clear();
    }

    private void removeDeadStars() {
        for (Star deadStar : deadStars) {
            stars.remove(deadStar);
        }
    }

    private void removePlayerDeadShots(LinkedList<Shot> playerDeadShots, LinkedList<Shot> playerShots) {
        for (Shot deadShot : playerDeadShots) {
            playerShots.remove(deadShot);
        }
    }

    private void removeEnemyDeadShots(LinkedList<Shot> enemyDeadShots, LinkedList<Shot> enemyShots) {
        for (Shot deadShot : enemyDeadShots) {
            enemyShots.remove(deadShot);
        }
    }

    private void removeDeadEnemies(LinkedList<Enemy> deadEnemies, LinkedList<Enemy> enemies) {
        for (Enemy deadEnemy : deadEnemies) {
            enemies.remove(deadEnemy);
        }
    }

    private void removeDeadExplosions(LinkedList<Enemy> deadExplosions, LinkedList<Enemy> explosions) {
        for (Enemy deadExplosion : deadExplosions) {
            explosions.remove(deadExplosion);
        }
    }

    private void renderHUD() {
        renderHP();
        renderAmmo();
    }

    private void renderHP() {
        gc.setFill(hpColor);
        gc.fillRect(10, WINDOW_HEIGHT-22, 152, 12);

        gc.setFill(gameBgColor);
        gc.fillRect(11, WINDOW_HEIGHT-21, 150, 10);

        gc.setFill(hpColor);
        gc.fillRect(11, WINDOW_HEIGHT-21, 150*player.healthCurrent/player.healthMax, 10);

        // HP icon
        gc.drawImage(HP_IMG, 10, WINDOW_HEIGHT-43);
        gc.setFont(Font.font("Verdana", 17.5));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(String.valueOf(player.healthCurrent), 29, WINDOW_HEIGHT-29);
    }

    private void renderAmmo() {
        gc.setFill(ammoColor);
        gc.fillRect(WINDOW_WIDTH-162, WINDOW_HEIGHT-22, 152, 12);

        gc.setFill(gameBgColor);
        gc.fillRect(WINDOW_WIDTH-161, WINDOW_HEIGHT-21, 150, 10);

        gc.setFill(ammoColor);
        if (player.reloading) {
            gc.setGlobalAlpha(0.3);
            gc.fillRect(WINDOW_WIDTH-11-150*(player.reloadTimeMax-player.reloadTimeCurrent)/player.reloadTimeMax, WINDOW_HEIGHT-21, 150*(player.reloadTimeMax-player.reloadTimeCurrent)/player.reloadTimeMax, 10);
            gc.setGlobalAlpha(1);
        } else {
            gc.fillRect(WINDOW_WIDTH-11-150*player.ammunitionCurrent/player.ammunitionMax, WINDOW_HEIGHT-21, 150*player.ammunitionCurrent/player.ammunitionMax, 10);
        }

        // Ammo icon
        gc.drawImage(AMMO_IMG, WINDOW_WIDTH-25, WINDOW_HEIGHT-43);
        gc.setFont(Font.font("Verdana", 17.5));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(String.valueOf(player.ammunitionCurrent) + "/" + String.valueOf(player.ammunitionMax), WINDOW_WIDTH-29, WINDOW_HEIGHT-29);
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
