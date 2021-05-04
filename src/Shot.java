import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Bullets (shots) from either the player or enemies.
 */
public class Shot extends Sprite {
    String origin; // String origin??
    Color color;
    static final int size = 8;
    public boolean toRemove;

    public Shot(int posX, int posY, int height, int width, int velocity) { // TODO: Add String origin
        super(posX, posY, height, width, velocity);
        this.origin = origin; // String origin
        this.color = Color.RED;
    }

    /**
     * Updates the y-position of the bullets
     */
    public void update() {
        posY -= velocity;
    }

    /**
     * Draws the bullets.
     * @param gc The canvas.
     * @param score The score.
     */
    public void draw(GraphicsContext gc, int score) {
        gc.setFill(Color.ORANGE);
        // TODO: Change once we've fixed inventory.
        if (score >= 60 && score <= 80 || score >= 150) {
            upgradeWeapon(gc);
        } else {
            shootNormalBullets(gc);
        }
    }

    private void upgradeWeapon(GraphicsContext gc) {
        gc.setFill(Color.YELLOWGREEN);
        velocity = 50;
        gc.fillRect(posX-5, posY-10, size+10, size+30);
    }

    private void shootNormalBullets(GraphicsContext gc) {
        gc.fillOval(posX, posY, size, size);
    }

    // TODO: Add method that handles collisions.

    public static int getSize() {
        return size;
    }

    public boolean getToRemove() {
        return toRemove;
    }
}