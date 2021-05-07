import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Bullets (shots) from either the player or enemies.
 */
public class Shot extends Sprite {
    String origin;
    Color color;
    int damage;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param height Height in y-axis.
     * @param width Width in x-axis.
     * @param velocity Speed.
     */
    public Shot(double posX, double posY, int height, int width, double velocity, String origin) {
        super(posX, posY, height, width, velocity);
        this.origin = origin;
        this.color = Color.RED;
        this.damage = 100;
    }

    /**
     * Updates the y-position of the bullets
     */
    public void update() {
        if (origin.equalsIgnoreCase("player")) {
            posY += velocity;
        } else {
            posY -= velocity;
        }
    }

    /**
     * Draws the bullets.
     * @param gc The canvas.
     */
    public void draw(GraphicsContext gc) {
        update();
        gc.setFill(Color.ORANGE);
        gc.fillRect(this.posX, this.posY, this.width, this.height);
        // TODO: Change once we've fixed inventory.
//        if (score >= 60 && score <= 80 || score >= 150) {
//            upgradeWeapon(gc);
//        } else {
//            shootNormalBullets(gc);
//        }
    }

    // TODO: Add method that handles collisions.
}