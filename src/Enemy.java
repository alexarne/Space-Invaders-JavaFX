import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Enemy extends Rocket {
    int windowHeight;
    boolean ableToShoot;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param height Height in y-axis.
     * @param width Width in x-axis.
     * @param velocity Speed.
     * @param img Image.
     * @param health Health.
     * @param ableToShoot Boolean that controls whether the enemy can shoot or not.
     * @param windowHeight Screen height.
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