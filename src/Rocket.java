import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Rocket extends Sprite {
    int explosionStep, health, cooldown, cooldownTracker;
    boolean exploding;
    Image img;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param height Height in y-axis.
     * @param width Width in x-axis.
     * @param velocity Speed.
     * @param img Image.
     * @param health Health.
     */
    public Rocket(double posX, double posY, int height, int width, double velocity, Image img, int health) {
        super(posX, posY, height, width, velocity);
        this.img = img;
        this.health = health;
        this.explosionStep = 0;
        this.exploding = false;
    }
}