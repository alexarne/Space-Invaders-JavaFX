import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Enemy extends Rocket {
    int windowHeight;

    public Enemy(int posX, int posY, int height, int width, int velocity, Image img, int health, int windowHeight) {
        super(posX, posY, height, width, velocity, img, health);
        this.windowHeight = windowHeight;
    }
}