import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Enemy extends Rocket {
    int windowHeight;
    boolean ableToShoot;

    /**
     * 
     * @param posX
     * @param posY
     * @param height
     * @param width
     * @param velocity
     * @param img
     * @param health
     * @param ableToShoot
     * @param windowHeight
     */
    public Enemy(int posX, int posY, int height, int width, int velocity, Image img, int health, boolean ableToShoot, int windowHeight) {
        super(posX, posY, height, width, velocity, img, health);
        this.windowHeight = windowHeight;
        this.ableToShoot = ableToShoot;
    }

    /**
     * Draws the enemy onto the canvas.
     * @param gc The canvas.
     */
    public void draw(GraphicsContext gc) {
        gc.drawImage(img, posX, posY);
    }
}