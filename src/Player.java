import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * The player.
 */
public class Player extends Rocket {
    int windowWidth;

    public Player(int posX, int posY, int height, int width, int velocity, Image img, int health, int windowWidth) {
        super(posX, posY, height, width, velocity, img, health);
        this.windowWidth = windowWidth;
    }

    /**
     * Draws the player onto the canvas.
     * @param gc The canvas.
     */
    public void draw(GraphicsContext gc) {
        gc.drawImage(img, posX, posY);
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
        if (posX + velocity > windowWidth - this.width) {
            this.posX = windowWidth - this.width;
        } else {
            this.posX += velocity;
        }
    }
}