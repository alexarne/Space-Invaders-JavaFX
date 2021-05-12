
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
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
    private Pane root;

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

        root = new Pane();
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
        bg.setFill(Color.WHITE);
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
        startGame.setOnMouseClicked(mouseEvent -> {
            // Animate the button
            animateButtonOut(startGame, true, animationDuration, interp, root);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != startGame) animateButtonOut(button, false, animationDuration, interp, root);
            }

            // Fade overlay
            Rectangle bgFade = new Rectangle();
            bgFade.setHeight(WINDOW_HEIGHT);
            bgFade.setWidth(WINDOW_WIDTH);
            bgFade.setFill(gameBgColor);
            bgFade.setOpacity(0);

            // Introduce level selection
            LevelLoader levelLoader = new LevelLoader(WINDOW_WIDTH, gameBgColor, rnd);
            PauseTransition[] p = new PauseTransition[]{
                    new PauseTransition()
            };
            p[0].setDuration(Duration.millis(100));
            p[0].play();
            int levelsPerRow = 3;
            PauseTransition pUpper = p[0];
            for (int i = 1; i <= levelLoader.getAmountOfLevels(); i++) {
                p = createLevelButton(i, p[0], bgFade, levelLoader, pUpper, levelsPerRow);
                if (i % levelsPerRow == 1) pUpper = p[1];
            }
        });
    }

    private PauseTransition[] createLevelButton(int level, PauseTransition pPrev, Rectangle bgFade, LevelLoader levelLoader, PauseTransition pUpper, int levelsPerRow) {
        double w = 80;
        double h = 80;
        int margin = 16;
        int border = 2;
        double totalW = w*levelsPerRow + margin*(levelsPerRow-1);
        double startX = WINDOW_WIDTH/2 - totalW/2;
        int startY = 160;

        double xSteps = (w + margin) * ((level - 1) % levelsPerRow);
        double ySteps = (h + margin) * ((level - 1) / levelsPerRow);

        Rectangle outRect = new Rectangle();
        outRect.setWidth(w);
        outRect.setHeight(h);
        outRect.setFill(Color.BLACK);
        outRect.setX(startX + xSteps);
        outRect.setY(startY + ySteps);

        Rectangle inRect = new Rectangle();
        inRect.setWidth(w-2*border);
        inRect.setHeight(h-2*border);
        inRect.setFill(Color.WHITE);
        inRect.setX(startX + xSteps + border);
        inRect.setY(startY + ySteps + border);

        Text label = centerTextInBox(String.valueOf(level), inRect);

        Rectangle marker = new Rectangle();
        marker.setWidth(w);
        marker.setHeight(h);
        marker.setOpacity(0);
        marker.setX(startX + xSteps);
        marker.setY(startY + ySteps);

        int millis = 250;
        double scaleIn = (w+margin)/w;
        ScaleTransition zoomIn1 = getScaleTransition(outRect, millis, scaleIn);
        ScaleTransition zoomIn2 = getScaleTransition(inRect, millis, scaleIn);
        ScaleTransition zoomIn3 = getScaleTransition(label, millis, scaleIn);
        ScaleTransition zoomIn4 = getScaleTransition(marker, millis, scaleIn);
        double scaleOut = 1;
        ScaleTransition zoomOut1 = getScaleTransition(outRect, millis, scaleOut);
        ScaleTransition zoomOut2 = getScaleTransition(inRect, millis, scaleOut);
        ScaleTransition zoomOut3 = getScaleTransition(label, millis, scaleOut);
        ScaleTransition zoomOut4 = getScaleTransition(marker, millis, scaleOut);

        marker.setOnMouseEntered(e -> {
            zoomIn1.play();
            zoomIn2.play();
            zoomIn3.play();
            zoomIn4.play();
        });
        marker.setOnMousePressed(e -> {
            outRect.setFill(Color.GREY);
            label.setFill(Color.GREY);
        });
        marker.setOnMouseExited(e -> {
            outRect.setFill(Color.BLACK);
            label.setFill(Color.BLACK);
            zoomOut1.play();
            zoomOut2.play();
            zoomOut3.play();
            zoomOut4.play();
        });
        marker.setOnMouseClicked(e -> beginGame(bgFade, levelLoader, level));

        int millis2 = 50;
        PauseTransition[] p2;
        if (level % levelsPerRow == 1) {
            p2 = new PauseTransition[]{
                    new PauseTransition(Duration.millis(millis2)),
                    new PauseTransition(Duration.millis(millis2))
            };
        } else {
            p2 = new PauseTransition[]{
                    new PauseTransition(Duration.millis(millis2))
            };
        }

        // Doesn't work even tho it should idk gonna fix some other time, used to stagger diagonally instead of linearly
        if (level % levelsPerRow == 1) {
            pUpper.setOnFinished(e -> {
                pUpper.getOnFinished();
                animateLevelIn(outRect, inRect, label, marker);
                root.getChildren().add(outRect);
                root.getChildren().add(inRect);
                root.getChildren().add(label);
                root.getChildren().add(marker);
                p2[0].play();
                p2[1].play();
            });
        } else {
            pPrev.setOnFinished(e -> {
                animateLevelIn(outRect, inRect, label, marker);
                root.getChildren().add(outRect);
                root.getChildren().add(inRect);
                root.getChildren().add(label);
                root.getChildren().add(marker);
                p2[0].play();
            });
        }

        // Return array of pause transitions, 2 each, one for downwards and one for rightwards to enable one pausetransition to play two other transitions
        return p2;
    }

    private void animateLevelIn(Rectangle outRect, Rectangle inRect, Text label, Rectangle marker) {
        int millis = 500;
        double scaleFrom = 0.75;

        scaleNodeIn(outRect, millis, scaleFrom);
        scaleNodeIn(inRect, millis, scaleFrom);
        scaleNodeIn(label, millis, scaleFrom);
        scaleNodeIn(marker, millis, scaleFrom);

        fadeNodeIn(outRect, millis);
        fadeNodeIn(label, millis);
    }

    private void scaleNodeIn(Node node, int millis, double scaleFrom) {
        ScaleTransition s = new ScaleTransition();
        s.setDuration(Duration.millis(millis));
        node.setScaleX(scaleFrom);
        node.setScaleY(scaleFrom);
        s.setToX(1);
        s.setToY(1);
        s.setNode(node);
        s.play();
    }

    private void fadeNodeIn(Node node, int millis) {
        FadeTransition f = new FadeTransition();
        f.setDuration(Duration.millis(millis));
        node.setOpacity(0);
        f.setToValue(1);
        f.setNode(node);
        f.play();
    }

    private ScaleTransition getScaleTransition(Node node, int millis, double scaleIn) {
        ScaleTransition scale = new ScaleTransition();
        scale.setDuration(Duration.millis(millis));
        scale.setToX(scaleIn);
        scale.setToY(scaleIn);
        scale.setNode(node);
        return scale;
    }

    private Text centerTextInBox(String text, Rectangle inRect) {
        Text label = new Text();
        label.setText(text);
        label.setFill(Color.BLACK);
        label.setFont(Font.font("Verdana", 35));
        label.setX(inRect.getX() + inRect.getWidth()/2 - label.getLayoutBounds().getWidth()/2);
        label.setY(inRect.getY() + inRect.getHeight()/2 + 10);
        return label;
    }

    private void beginGame(Rectangle bgFade, LevelLoader levelLoader, int level) {
        root.getChildren().add(bgFade);
        GameMechanics gameMechanics = new GameMechanics(WINDOW_WIDTH, WINDOW_HEIGHT, window, gameBgColor, rnd, levelLoader, level);
        FadeTransition transitionBackground = new FadeTransition();
        transitionBackground.setDuration(Duration.millis(300));
        transitionBackground.setToValue(1);
        transitionBackground.setNode(bgFade);
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
        highscore.setOnMouseClicked(mouseEvent -> {
            // Animate the button
            animateButtonOut(highscore, true, animationDuration, interp, root);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != highscore) animateButtonOut(button, false, animationDuration, interp, root);
            }

            // TODO
        });
    }

    private void handleInventory(Text inventory, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        inventory.setOnMouseClicked(mouseEvent -> {
            // Animate the button
            animateButtonOut(inventory, true, animationDuration, interp, root);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != inventory) animateButtonOut(button, false, animationDuration, interp, root);
            }

            // TODO
        });
    }

    private void handleAchievements(Text achievements, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        achievements.setOnMouseClicked(mouseEvent -> {
            // Animate the button
            animateButtonOut(achievements, true, animationDuration, interp, root);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != achievements) animateButtonOut(button, false, animationDuration, interp, root);
            }

            // TODO
        });
    }

    private void handleSettings(Text settings, ArrayList<Text> textArr, Duration animationDuration, Interpolator interp) {
        settings.setOnMouseClicked(mouseEvent -> {
            // Animate the button
            animateButtonOut(settings, true, animationDuration, interp, root);
            // Animate the other buttons
            for (Text button : textArr) {
                if (button != settings) animateButtonOut(button, false, animationDuration, interp, root);
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
     * @param root The root Pane for the window.
     */
    public void animateButtonOut(Text button, boolean wasPressed, Duration animationDuration, Interpolator interp, Pane root) {
        double value = wasPressed ? .2 : -.07;
        transitionScale(button, animationDuration, interp, value);
        transitionFade(button, animationDuration, interp, root);
    }

    private void transitionFade(Text button, Duration animationDuration, Interpolator interp, Pane root) {
        FadeTransition transition2 = new FadeTransition();
        transition2.setDuration(animationDuration);
        transition2.setToValue(0);
        transition2.setNode(button);
        transition2.setInterpolator(interp);
        transition2.setOnFinished(e -> root.getChildren().remove(button));
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