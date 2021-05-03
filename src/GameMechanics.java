import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class GameMechanics {
    // TODO create mvp
    
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private GraphicsContext gc;
    private Scene gameScene;
    private Color gameBgColor;

    public GameMechanics(int width, int height, GraphicsContext gc, Scene gameScene, Color color) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
        this.gc = gc;
        this.gameScene = gameScene;
        gameBgColor = color;
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

        StackPane root = new StackPane(canvas);
        gameScene = new Scene(root);
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
        boolean dead;

        public Sprite(int posX, int posY, int height, int width, int velocity) {
            this.posX = posX;
            this.posY = posY;
            this.height = height;
            this.width = width;
            this.velocity = velocity;
            this.dead = false;
        }

        /**
         * Checks if this object and another object has collided. Treats sprites as rectangles.
         * @param who The other object to do the collision check with.
         * @return True if the two objects have collided, false if not.
         */
        public boolean hasCollided(Sprite who) {
            // This Sprite's restraints:
            int xLeft1 = this.posX;
            int xRight1 = this.posX + this.width;
            int yUp1 = this.posY;
            int yDown1 = this.posY + this.height;
            // The other sprite's restraints:
            int xLeft2 = who.posX;
            int xRight2 = who.posX + who.width;
            int yUp2 = who.posY;
            int yDown2 = who.posY + who.height;

            return (xLeft1 < xRight2) && (xLeft2 < xRight1) && (yUp1 < yDown2) && (yUp2 < yDown1);
        }
    }

    /**
     * Enemies travelling towards the player.
     */
    public class Enemy extends Sprite {
        int explosionStep, health;
        boolean exploding;
        Image img;

        public Enemy(int posX, int posY, int height, int width, int velocity, Image img, int health) {
            super(posX, posY, height, width, velocity);
            this.img = img;
            this.health = health;
            this.explosionStep = 0;
            this.exploding = false;
        }
    }

    /**
     * The player.
     */
    public class Player extends Enemy {

        public Player(int posX, int posY, int height, int width, int velocity, Image img, int health) {
            super(posX, posY, height, width, velocity, img, health);
        }

        /**
         * Move the player to the left with respect to its velocity.
         */
        public void moveLeft() {
            if (posX <= velocity) {
                this.posX = 0;
            } else {
                this.posX -= velocity;
            }
        }

        /**
         * Move the player to the right with respect to its velocity.
         */
        public void moveRight() {
            if (posX >= velocity - this.width) {
                this.posX = velocity - this.width;
            } else {
                this.posX += velocity;
            }
        }
    }

    /**
     * Bullets (shots) from either the player or enemies.
     */
    public class Shot extends Sprite {
        String origin;
        Color color;

        public Shot(int posX, int posY, int height, int width, int velocity, String origin) {
            super(posX, posY, height, width, velocity);
            this.origin = origin;
            this.color = Color.RED;
        }
    }
}
