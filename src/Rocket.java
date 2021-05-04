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

    public Shot shoot() {
        int bulletVelocity = 2;
        int xPos = posX + height / 2 - Shot.getSize() / 2;
        int yPos = (int) posY - Shot.getSize(); // Needs to be converted to int instead of double
        return new Shot(xPos, yPos, height, width, bulletVelocity);
    }
}