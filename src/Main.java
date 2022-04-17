
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main extends Application {

    static final int WINDOW_HEIGHT = (int) Screen.getPrimary().getBounds().getHeight() - 180;
    static final int WINDOW_WIDTH = 600;
    Stage window;
    Scene mainScene, gameScene;
    private Color gameBgColor = Color.grayRgb(20);
    private Random rnd;
    private Pane root;

    private boolean levelsLoaded;

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

        this.rnd = new Random();
        root = new Pane();
        mainScene = new Scene(root);

        mainMenu(true);

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

        this.rnd = new Random();
        root = new Pane();
        mainScene = new Scene(root);

        mainMenu(true);
    }

    /**
     * Load the Main Menu.
     * @param fullStart If true, animates the main menu title too.
     */
    public void mainMenu(boolean fullStart) {
        Rectangle bg = getBackground();

        Text menuText = makeMenuTextTitle("Main Menu", 100, 40, !fullStart);
        menuText.setId("MainMenu");
        Text startGame = makeMenuTextTitle("Start Game", 150, 20, false);
        Text inventory = makeMenuTextTitle("Inventory", 190, 20, false);
        Text achievements = makeMenuTextTitle("Achievements", 230, 20, false);
        Text highscore = makeMenuTextTitle("Highscore", 270, 20, false);
        Text settings = makeMenuTextTitle("Settings", 310, 20, false);

        ArrayList<Text> textArr = new ArrayList<>(
                Arrays.asList(
                        startGame,
                        inventory,
                        achievements,
                        highscore,
                        settings
                )
        );
        showButtons(menuText, fullStart, textArr);
        highlightButtons(textArr);

        // Configure button interactions
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

    private void showButtons(Text menuText, boolean includeMain, ArrayList<Text> textArr) {
        Duration duration = Duration.millis(500);
        Interpolator interp = Interpolator.EASE_OUT;

        PauseTransition last = includeMain ? staggeredButtonAnimation(menuText, duration, interp, null) : null;

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
     * @param show If true, shows the title by giving it 1 as opacity.
     * @return The Text object.
     */
    public Text makeMenuTextTitle(String s, int posY, int fontSize, boolean show) {
        Text label = new Text();
        label.setText(s);
        label.setFill(Color.BLACK);
        label.setFont(Font.font("Sitka Small", fontSize));
        label.setX(WINDOW_WIDTH / 2 - label.getLayoutBounds().getWidth() / 2);
        label.setY(posY);
        label.setOpacity(show ? 1 : 0);
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
            this.levelsLoaded = false;
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

            int levelsPerRow = 3;
            int levelsPerCol = 1 + levelLoader.getAmountOfLevels()/levelsPerRow;
            double w = 80;
            double h = 80;
            int margin = 16;
            int border = 2;
            double totalW = w*levelsPerRow + margin*(levelsPerRow-1);
            double startX = WINDOW_WIDTH/2 - totalW/2;
            int startY = 174;
            int millis = 150;
            double scaleIn = (w+margin)/w;

            double boxHeight = 30;
            double xPos = startX;
            // Make "Back" button at the bottom
            double yPos = startY + levelsPerCol*margin + levelsPerCol*w;
            Rectangle backButton = makeBackHeader(xPos, yPos, totalW, boxHeight, border, "Return", millis, (totalW+margin)/totalW);
            // Make "Select Level" box title
            yPos = startY - margin - boxHeight;
            makeSelectLevelHeader(xPos, yPos, totalW, boxHeight, border, "Select level");

            PauseTransition[] p = new PauseTransition[]{
                    new PauseTransition()
            };
            p[0].setDuration(Duration.millis(100));
            p[0].play();
            PauseTransition pUpper = p[0];
            for (int level = 1; level <= levelLoader.getAmountOfLevels(); level++) {
                double x = startX + (w + margin) * ((level - 1) % levelsPerRow);
                double y = startY + (h + margin) * ((level - 1) / levelsPerRow);
                p = createLevelButton(level, p[0], bgFade, levelLoader, pUpper, levelsPerRow, x, y, w, h, border, millis, scaleIn);
                if (level % levelsPerRow == 1) pUpper = p[1];
                // Prohibit user from selecting a level before all levels have been added to root in order to circumvent bug
                if (level == levelLoader.getAmountOfLevels()) p[0].setOnFinished(e -> this.levelsLoaded = true);
            }

            backButton.setOnMouseClicked(e -> {
                int durationMillis = 200;

                for (Node node : root.getChildren()) {
                    if (node.getId() != null && node.getId().equals("MainMenu")) continue;      // Don't apply to main menu title
                    double scaleByValue = -24/node.getLayoutBounds().getWidth();

                    if (node.getId() == null || !node.getId().equals("InnerRectangle")) transitionFade(node, Duration.millis(durationMillis), Interpolator.EASE_OUT, root);
                    transitionScale(node, Duration.millis(durationMillis), Interpolator.EASE_OUT, scaleByValue);
                }

                PauseTransition pBack = new PauseTransition(Duration.millis(durationMillis));
                pBack.setOnFinished(f -> mainMenu(false));
                pBack.play();
            });
        });
    }

    private PauseTransition[] createLevelButton(int level, PauseTransition pPrev, Rectangle bgFade, LevelLoader levelLoader, PauseTransition pUpper, int levelsPerRow,
                                                double x, double y, double w, double h, int border, int millis, double scaleIn) {
        Rectangle outRect = new Rectangle();
        outRect.setWidth(w);
        outRect.setHeight(h);
        outRect.setFill(Color.BLACK);
        outRect.setX(x);
        outRect.setY(y);

        Rectangle inRect = new Rectangle();
        inRect.setWidth(w-2*border);
        inRect.setHeight(h-2*border);
        inRect.setFill(Color.WHITE);
        inRect.setX(x + border);
        inRect.setY(y + border);

        Text label = centerTextInBox(String.valueOf(level), inRect);

        Rectangle marker = new Rectangle();
        marker.setWidth(w);
        marker.setHeight(h);
        marker.setOpacity(0);
        marker.setX(x);
        marker.setY(y);

        outRect.setId("OuterRectangle");
        inRect.setId("InnerRectangle");
        marker.setId("MarkerRectangle");

        handleButtonHoverOnLevelSel(millis, scaleIn, outRect, inRect, label, marker);
        marker.setOnMouseClicked(e -> {
            if (levelsLoaded) beginGame(bgFade, levelLoader, level);
        });

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

    private void handleButtonHoverOnLevelSel(int millis, double scaleIn, Rectangle outRect, Rectangle inRect, Text label, Rectangle marker) {
        ScaleTransition zoomIn1 = getScaleTransition(outRect, millis, scaleIn);
        ScaleTransition zoomIn2 = getScaleTransition(inRect, millis, scaleIn);
        ScaleTransition zoomIn3 = getScaleTransition(label, millis, scaleIn);
        ScaleTransition zoomIn4 = getScaleTransition(marker, millis, scaleIn);
        ScaleTransition zoomOut1 = getScaleTransition(outRect, millis, 1);
        ScaleTransition zoomOut2 = getScaleTransition(inRect, millis, 1);
        ScaleTransition zoomOut3 = getScaleTransition(label, millis, 1);
        ScaleTransition zoomOut4 = getScaleTransition(marker, millis, 1);

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
    }

    private void makeSelectLevelHeader(double xPos, double yPos, double w, double h, double border, String text) {
        Rectangle outer1 = new Rectangle();
        outer1.setFill(Color.BLACK);
        outer1.setWidth(w);
        outer1.setHeight(h);
        outer1.setX(xPos);
        outer1.setY(yPos);

        Rectangle inner1 = new Rectangle();
        inner1.setFill(Color.WHITE);
        inner1.setWidth(w - 2* border);
        inner1.setHeight(h - 2* border);
        inner1.setX(xPos + border);
        inner1.setY(yPos + border);

        Text label1 = new Text();
        label1.setText(text);
        label1.setFont(Font.font("Sitka Small", 20));
        label1.setX(WINDOW_WIDTH / 2 - label1.getLayoutBounds().getWidth()/2);
        label1.setY(yPos + h - (int) (1.6*(h - label1.getLayoutBounds().getHeight())/2));

        inner1.setId("InnerRectangle");

        int millis = 500;
        double scaleFrom = 0.85;
        scaleNodeIn(outer1, millis, scaleFrom);
        scaleNodeIn(inner1, millis, scaleFrom);
        scaleNodeIn(label1, millis, scaleFrom);
        fadeNodeIn(outer1, millis);
        fadeNodeIn(label1, millis);
        root.getChildren().addAll(outer1, inner1, label1);
    }

    private Rectangle makeBackHeader(double xPos, double yPos, double w, double h, double border, String text, int millis, double scaleOnHover) {
        Rectangle outer1 = new Rectangle();
        outer1.setFill(Color.BLACK);
        outer1.setWidth(w);
        outer1.setHeight(h);
        outer1.setX(xPos);
        outer1.setY(yPos);

        Rectangle inner1 = new Rectangle();
        inner1.setFill(Color.WHITE);
        inner1.setWidth(w - 2* border);
        inner1.setHeight(h - 2* border);
        inner1.setX(xPos + border);
        inner1.setY(yPos + border);

        Text label1 = new Text();
        label1.setText(text);
        label1.setFont(Font.font("Sitka Small", 20));
        label1.setX(WINDOW_WIDTH / 2 - label1.getLayoutBounds().getWidth()/2);
        label1.setY(yPos + h - (int) (1.6*(h - label1.getLayoutBounds().getHeight())/2));

        Rectangle marker = new Rectangle();
        marker.setWidth(w);
        marker.setHeight(h);
        marker.setOpacity(0);
        marker.setX(xPos);
        marker.setY(yPos);

        inner1.setId("InnerRectangle");

        int millis2 = 500;
        double scaleFrom = 0.85;
        scaleNodeIn(outer1, millis2, scaleFrom);
        scaleNodeIn(inner1, millis2, scaleFrom);
        scaleNodeIn(label1, millis2, scaleFrom);
        scaleNodeIn(marker, millis2, scaleFrom);
        fadeNodeIn(outer1, millis2);
        fadeNodeIn(label1, millis2);
        root.getChildren().addAll(outer1, inner1, label1, marker);

        handleButtonHoverOnLevelSel(millis, scaleOnHover, outer1, inner1, label1, marker);

        return marker;
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

    private void transitionFade(Node node, Duration animationDuration, Interpolator interp, Pane root) {
        FadeTransition transition2 = new FadeTransition();
        transition2.setDuration(animationDuration);
        transition2.setToValue(0);
        transition2.setNode(node);
        transition2.setInterpolator(interp);
        transition2.setOnFinished(e -> root.getChildren().remove(node));
        transition2.play();
    }

    private void transitionScale(Node node, Duration animationDuration, Interpolator interp, double value) {
        ScaleTransition transition1 = new ScaleTransition();
        transition1.setDuration(animationDuration);
        transition1.setByX(value);
        transition1.setByY(value);
        transition1.setNode(node);
        transition1.setInterpolator(interp);
        transition1.play();
    }
}