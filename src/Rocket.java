import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Rocket extends Sprite {
    int explosionStep, health;
    boolean exploding;
    Image img;

    public Rocket(int posX, int posY, int height, int width, int velocity, Image img, int health) {
        super(posX, posY, height, width, velocity);
        this.img = img;
        this.health = health;
        this.explosionStep = 0;
        this.exploding = false;
    }
}