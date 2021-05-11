
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main extends Application {

    static final int WINDOW_HEIGHT = 900;
    static final int WINDOW_WIDTH = 600;
    Stage window;
    Scene mainScene, gameScene;
    private Color gameBgColor = Color.grayRgb(20);
    private Random rnd;
    private Group root;

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
        window.sizeToScene();

        mainMenu();

        window.setScene(mainScene);
        window.show();
    }

    public Scene getMainScene() {
        return mainScene;
    }

    /**
     * Load the Main Menu.
     */
    public void mainMenu(Stage window) {
        this.window = window;
        mainMenu();
    }

    /**
     * Load the Main Menu.
     */
    public void mainMenu() {
        this.rnd = new Random();

        root = new Group();
        mainScene = new Scene(root);

        Rectangle bg = getBackground();

        Text menuText = makeMenuTextTitle("Main Menu", 100, 40);
        Text startGame = makeMenuTextTitle("Start Game", 150, 20);
        Text inventory = makeMenuTextTitle("Inventory", 190, 20);
        Text achievements = makeMenuTextTitle("Achievements", 230, 20);
        Text highscore = makeMenuTextTitle("Highscore", 270, 20);
        Text settings = makeMenuTextTitle("Settings", 310, 20);

        ArrayList<Text> textArr = new ArrayList<>(
                Arrays.asList(
                        startGame,
                        inventory,
                        achievements,
                        highscore,
                        settings
                )
        );
        showButtons(menuText, textArr);
        highlightButtons(textArr);

        // Configure animations
        Duration animationDuration = Duration.millis(300);
        Interpolator interp = Interpolator.EASE_OUT;

        handleStartGame(bg, menuText, startGame, textArr, animationDuration, interp);
        handleHighscore(highscore, textArr, animationDuration, interp);
        handleInventory(inventory, textArr, animationDuration, interp);
        handleAchievements(achievements, textArr, animationDuration, interp);
        handleSettings(settings, textArr, animationDuration, interp);

        root.getChildren().add(bg);
        root.getChildren().add(menuText);
        for (Text label : textArr) {
            root.getChildren().add(label);
        }
    }

    private void showButtons(Text menuText, ArrayList<Text> textArr) {
        Duration duration = Duration.millis(500);
        Interpolator interp = Interpolator.EASE_OUT;

        PauseTransition last = staggeredButtonAnimation(menuText, duration, interp, null);
        for (Text label : textArr) {
            last = staggeredButtonAnimation(label, duration, interp, last);
        }
    }

    private PauseTransition staggeredButtonAnimation(Text label, Duration duration, Interpolator interp, PauseTransition last) {
        FadeTransition fade = new FadeTransition();
        fade.setDuration(duration);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(label);
        fade.setInterpolator(interp);
        ScaleTransition scale = new ScaleTransition();
        scale.setDuration(duration);
        scale.setFromX(1.1);
        scale.setFromY(1.1);
        scale.setToX(1);
        scale.setToY(1);
        scale.setNode(label);
        scale.setInterpolator(interp);

        Duration d = duration.divide(5);
        PauseTransition p = new PauseTransition(d);
        if (last != null) {
            last.setOnFinished(e -> {
                fade.play();
                scale.play();
                p.play();
            });
        } else {
            fade.play();
            scale.play();
            p.play();
        }

        return p;
    }

    private Rectangle getBackground() {
        Rectangle bg = new Rectangle();
        bg.setHeight(WINDOW_HEIGHT);
        bg.setWidth(WINDOW_WIDTH);
        bg.setFill(gameBgColor);
        bg.setOpacity(0);
        return bg;
    }

    /**
     * Configure a Text object.
     * @param s The title for the Text object.
     * @param posY The y-position for the Text object.
     * @return The Text object.
     */
    public Text makeMenuTextTitle(String s, int posY, int fontSize) {
        Text label = new Text();
        label.setText(s);
        label.setFill(Color.BLACK);
        label.setFont(Font.font("Sitka Small", fontSize));
        label.setX(WINDOW_WIDTH / 2 - label.getLayoutBounds().getWidth() / 2);
        label.setY(posY);
        label.setOpacity(0);
        return label;
    }

    /**
     * Makes it so that all buttons in the array get highlighted when hovered over.
     * @param arr An array of all the buttons
     */
    public void highlightButtons(ArrayList<Text> arr) {
        for (Text button : arr) {
            button.setOnMouseEntered(mouseEvent -> button.setFill(Color.GREY));
            button.setOnMouseExited(mouseEvent -> button.setFill(Color.BLACK));
        }
    }

    // Animate on button press and take to game.
    private void handleStartGame(Rectangle bg, Text menuText, Text startGame, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        int totalW = 100*3 + 30*2;
        int begin = WINDOW_WIDTH/2 - totalW/2;
        int next = 130;
        Rectangle level1 = new Rectangle();
        level1.setFill(Color.BLACK);
        level1.setWidth(100);
        level1.setHeight(100);
        level1.setX(begin);
        level1.setY(150);
        root.getChildren().add(level1);
        Rectangle level2 = new Rectangle();
        level2.setFill(Color.BLACK);
        level2.setWidth(100);
        level2.setHeight(100);
        level2.setX(begin+next);
        level2.setY(150);
        root.getChildren().add(level2);
        Rectangle level3 = new Rectangle();
        level3.setFill(Color.BLACK);
        level3.setWidth(100);
        level3.setHeight(100);
        level3.setX(begin+next*2);
        level3.setY(150);
        root.getChildren().add(level3);

        startGame.setOnMouseReleased(mouseEvent -> {
            // Animate the button
            animateButton(startGame, true, animationDuration, interp);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != startGame) animateButton(button, false, animationDuration, interp);
            }
            transitionFade(menuText, animationDuration, interp);

            // Introduce level selection
            LevelLoader levelLoader = new LevelLoader(WINDOW_WIDTH, gameBgColor, rnd);


            beginGame(bg);
        });
    }

    private void beginGame(Rectangle bg) {
        GameMechanics gameMechanics = new GameMechanics(WINDOW_WIDTH, WINDOW_HEIGHT, window, gameBgColor, rnd);
        FadeTransition transitionBackground = new FadeTransition();
        transitionBackground.setDuration(Duration.millis(300));
        transitionBackground.setToValue(1);
        transitionBackground.setNode(bg);
        transitionBackground.setInterpolator(Interpolator.EASE_BOTH);
        transitionBackground.setOnFinished(e -> {
            gameMechanics.load();
            gameScene = gameMechanics.getGameScene();
            window.setScene(gameScene);
            gameMechanics.startGame();
        }); // switches to game scene
        transitionBackground.play();

        gameMechanics.animatePlayerIn();
    }

    private void handleHighscore(Text highscore, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        highscore.setOnMouseReleased(mouseEvent -> {
            // Animate the button
            animateButton(highscore, true, animationDuration, interp);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != highscore) animateButton(button, false, animationDuration, interp);
            }

            // TODO
        });
    }

    private void handleInventory(Text inventory, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        inventory.setOnMouseReleased(mouseEvent -> {
            // Animate the button
            animateButton(inventory, true, animationDuration, interp);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != inventory) animateButton(button, false, animationDuration, interp);
            }

            // TODO
        });
    }

    private void handleAchievements(Text achievements, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        achievements.setOnMouseReleased(mouseEvent -> {
            // Animate the button
            animateButton(achievements, true, animationDuration, interp);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != achievements) animateButton(button, false, animationDuration, interp);
            }

            // TODO
        });
    }

    private void handleSettings(Text settings, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        settings.setOnMouseReleased(mouseEvent -> {
            // Animate the button
            animateButton(settings, true, animationDuration, interp);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != settings) animateButton(button, false, animationDuration, interp);
            }

            // TODO
        });
    }

    /**
     * Animate a given button according to one of two predetermined animations.
     * @param button The button which shall be animated.
     * @param wasPressed Whether the button was pressed or not (determines animation).
     * @param animationDuration The duration of the animation.
     * @param interp The interpolation of the animation.
     */
    public void animateButton(Text button, boolean wasPressed, Duration animationDuration, Interpolator interp) {
        double value = wasPressed ? .2 : -.07;
        transitionScale(button, animationDuration, interp, value);
        transitionFade(button, animationDuration, interp);
    }

    private void transitionFade(Text button, Duration animationDuration, Interpolator interp) {
        FadeTransition transition2 = new FadeTransition();
        transition2.setDuration(animationDuration);
        transition2.setToValue(0);
        transition2.setNode(button);
        transition2.setInterpolator(interp);
        transition2.play();
    }

    private void transitionScale(Text button, Duration animationDuration, Interpolator interp, double value) {
        ScaleTransition transition1 = new ScaleTransition();
        transition1.setDuration(animationDuration);
        transition1.setByX(value);
        transition1.setByY(value);
        transition1.setNode(button);
        transition1.setInterpolator(interp);
        transition1.play();
    }
}