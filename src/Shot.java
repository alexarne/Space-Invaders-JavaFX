import javafx.scene.paint.Color;

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