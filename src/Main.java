
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {

    static final Image PLAYER_IMG = new Image("Assets/Images/rocketclean64.png");
    static final int WINDOW_HEIGHT = 900;
    static final int WINDOW_WIDTH = 600;
    Stage window;
    Scene mainScene, gameScene;
    private GraphicsContext gc;
    private Color gameBgColor = Color.grayRgb(20);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        window = stage;

        Image icon = new Image("Assets/Images/rocketoutline.png");
        window.getIcons().add(icon);
        window.setTitle("Space Invaders");

        window.setHeight(WINDOW_HEIGHT);
        window.setWidth(WINDOW_WIDTH);
        window.setResizable(false);

        mainMenu();

        window.setScene(mainScene);
        window.show();
    }

    /**
     * Load the Main Menu.
     */
    public void mainMenu() {
        Group root = new Group();
        mainScene = new Scene(root);

        Rectangle bg = new Rectangle();
        bg.setHeight(WINDOW_HEIGHT);
        bg.setWidth(WINDOW_WIDTH);
        bg.setFill(Color.WHITE);

        Text menuText = new Text();
        menuText.setText("Main Menu");
        menuText.setFill(Color.BLACK);
        menuText.setFont(Font.font("Sitka Small", 40));
        menuText.setX((window.getWidth()-16) / 2 - menuText.getLayoutBounds().getWidth() / 2);
        menuText.setY(100);

        Text startGame = new Text();
        startGame.setText("Start Game");
        startGame.setFill(Color.BLACK);
        startGame.setFont(Font.font("Sitka Small", 20));
        startGame.setX((window.getWidth()-16) / 2 - startGame.getLayoutBounds().getWidth() / 2);
        startGame.setY(150);

        Text inventory = new Text();
        inventory.setText("Inventory");
        inventory.setFill(Color.BLACK);
        inventory.setFont(Font.font("Sitka Small", 20));
        inventory.setX((window.getWidth()-16) / 2 - inventory.getLayoutBounds().getWidth() / 2);
        inventory.setY(190);

        Text achievements = new Text();
        achievements.setText("Achievements");
        achievements.setFill(Color.BLACK);
        achievements.setFont(Font.font("Sitka Small", 20));
        achievements.setX((window.getWidth()-16) / 2 - achievements.getLayoutBounds().getWidth() / 2);
        achievements.setY(230);

        Text highscore = new Text();
        highscore.setText("Highscore");
        highscore.setFill(Color.BLACK);
        highscore.setFont(Font.font("Sitka Small", 20));
        highscore.setX((window.getWidth()-16) / 2 - highscore.getLayoutBounds().getWidth() / 2);
        highscore.setY(270);

        ArrayList<Text> textArr = new ArrayList<>(
                Arrays.asList(
                        startGame,
                        highscore,
                        inventory,
                        achievements
                )
        );
        mainMenuSetupHighlightButtons(textArr);

        Duration animationDuration = Duration.millis(300);
        Interpolator interp = Interpolator.EASE_OUT;

        startGame.setOnMouseReleased(mouseEvent -> {
            mainMenuButtonPressed(startGame, highscore, inventory, achievements, animationDuration, interp);

            // Animate the background
            FillTransition transitionBackground = new FillTransition();
            transitionBackground.setDuration(Duration.millis(300));
            transitionBackground.setToValue(gameBgColor);
            transitionBackground.setShape(bg);
            transitionBackground.setInterpolator(Interpolator.EASE_BOTH);
            transitionBackground.play();

            // Wait
            PauseTransition p = new PauseTransition(animationDuration);
            p.setOnFinished(e -> window.setScene(gameScene));
            p.play();

            game();
        });
        highscore.setOnMouseReleased(mouseEvent -> {
            mainMenuButtonPressed(highscore, startGame, inventory, achievements, animationDuration, interp);

            // TODO
        });
        inventory.setOnMouseReleased(mouseEvent -> {
            mainMenuButtonPressed(inventory, startGame, highscore, achievements, animationDuration, interp);

            // TODO
        });
        achievements.setOnMouseReleased(mouseEvent -> {
            mainMenuButtonPressed(achievements, startGame, inventory, highscore, animationDuration, interp);

            // TODO
        });

        root.getChildren().add(bg);
        root.getChildren().add(menuText);
        root.getChildren().add(inventory);
        root.getChildren().add(achievements);
        root.getChildren().add(highscore);
        root.getChildren().add(startGame);
    }

    /**
     * Makes it so that all buttons in the array get highlighted when hovered over.
     * @param arr An array of all the buttons
     */
    public void mainMenuSetupHighlightButtons(ArrayList<Text> arr) {
        for (Text button : arr) {
            button.setOnMouseEntered(mouseEvent -> button.setFill(Color.GREY));
            button.setOnMouseExited(mouseEvent -> button.setFill(Color.BLACK));
        }
    }

    /**
     * Makes a separate animation for the button if the button is clicked, and another animation
     * for the buttons that didn't get clicked.
     * @param clickedButton The button that is clicked.
     * @param n1 The first button that isn't clicked.
     * @param n2 The second button that isn't clicked.
     * @param n3 The third button that isn't clicked.
     * @param animationDuration The duration of the animation.
     * @param interp The interpolation method used for the animation.
     */
    public void mainMenuButtonPressed(Text clickedButton, Text n1, Text n2, Text n3, Duration animationDuration, Interpolator interp) {
        // Animate the clicked button
        ScaleTransition transition1 = new ScaleTransition();
        transition1.setDuration(animationDuration);
        transition1.setByX(.2);
        transition1.setByY(.2);
        transition1.setNode(clickedButton);
        transition1.setInterpolator(interp);
        transition1.play();
        FadeTransition transition2 = new FadeTransition();
        transition2.setDuration(animationDuration);
        transition2.setToValue(0);
        transition2.setNode(clickedButton);
        transition2.setInterpolator(interp);
        transition2.play();

        // Animate the other buttons
        // n1
        ScaleTransition transition3 = new ScaleTransition();
        transition3.setDuration(animationDuration);
        transition3.setByX(-.07);
        transition3.setByY(-.07);
        transition3.setNode(n1);
        transition3.setInterpolator(interp);
        transition3.play();
        FadeTransition transition4 = new FadeTransition();
        transition4.setDuration(animationDuration);
        transition4.setToValue(0);
        transition4.setNode(n1);
        transition4.setInterpolator(interp);
        transition4.play();
        // n2
        ScaleTransition transition5 = new ScaleTransition();
        transition5.setDuration(animationDuration);
        transition5.setByX(-.07);
        transition5.setByY(-.07);
        transition5.setNode(n2);
        transition5.setInterpolator(interp);
        transition5.play();
        FadeTransition transition6 = new FadeTransition();
        transition6.setDuration(animationDuration);
        transition6.setToValue(0);
        transition6.setNode(n2);
        transition6.setInterpolator(interp);
        transition6.play();
        // n3
        ScaleTransition transition7 = new ScaleTransition();
        transition7.setDuration(animationDuration);
        transition7.setByX(-.07);
        transition7.setByY(-.07);
        transition7.setNode(n3);
        transition7.setInterpolator(interp);
        transition7.play();
        FadeTransition transition8 = new FadeTransition();
        transition8.setDuration(animationDuration);
        transition8.setToValue(0);
        transition8.setNode(n3);
        transition8.setInterpolator(interp);
        transition8.play();

        // Wait
        PauseTransition p = new PauseTransition(animationDuration);
        p.setOnFinished(e -> window.setScene(gameScene));
        p.play();
    }

    /**
     * Start the game.
     */
    public void game() {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        gameSetup();

//        window.setScene(new Scene(new StackPane(canvas)));

        StackPane root = new StackPane(canvas);
        gameScene = new Scene(root);

//        Group root = new Group();
//        gameScene = new Scene(root, Color.BLACK);
//
//        Text text = new Text();
//        text.setText("lola issa game");
//        text.setFill(Color.WHITE);
//        text.setX(250);
//        text.setY(350);
//        text.setFont(Font.font("Sitka Small", 20));
//
//        Rectangle rectangle = new Rectangle(50, 50, 64, 64);
//        rectangle.setFill(Color.BLUE);
//        rectangle.setStroke(Color.GREEN);
//        rectangle.setStrokeWidth(10);
//
//        root.getChildren().add(rectangle);
//        root.getChildren().add(text);
    }

    /**
     * Initialize everything that has to be initialized for the game to begin.
     */
    public void gameSetup() {

    }

    /**
     * Handles rendering and updates.
     * @param gc The canvas to draw on.
     */
    public void run(GraphicsContext gc) {
        gc.setFill(gameBgColor);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

    }

    /**
     * Any substantial object which is drawn: Player, Enemies, Bullets, Stars, etc..
     */
    public class Sprite {
        int posX, posY, height, width, velocity;

        public Sprite(int posX, int posY, int height, int width, int velocity) {
            this.posX = posX;
            this.posY = posY;
            this.height = height;
            this.width = width;
            this.velocity = velocity;
        }

        /**
         * Checks if this object and another object has collided.
         * @param s The other object to do the collision check with.
         * @return True if the two objects have collided, false if not.
         */
        public boolean hasCollided(Sprite s) {
            return false;
        }
    }

    // Player
    public class Player {
        int posX, posY, size, explosionStep;
        boolean exploding, dead;
        Image img;

        // Constructor
        public Player(int posX, int posY, int size, Image image) {
            this.posX = posX;
            this.posY = posY;
            this.size = size;
            this.img = image;
            explosionStep = 0;
        }
    }
}

//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//    }