import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class GameMechanics {
    // TODO create mvp
    
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private GraphicsContext gc;
    private Scene gameScene;
    private Color gameBgColor;

    public GameMechanics(int width, int height, Color color) {
        this.WINDOW_WIDTH = width;
        this.WINDOW_HEIGHT = height;
        this.gameBgColor = color;
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
        StackPane root = new StackPane(canvas);
        gameScene = new Scene(root);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> run(gc)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        gameSetup();
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
        gc.setFill(Color.RED);
        gc.fillOval(100, 100, 200, 100);
        gc.fillRect(100, 100, 100, 100);

    }
}
