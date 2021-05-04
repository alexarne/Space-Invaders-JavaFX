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
        if (scoreIsBetween50and70orGreaterThan120(score)) {
            enableWeaponUpgrade(gc);
        } else {
            shootNormalBullets(gc);
        }
    }

    private boolean scoreIsBetween50and70orGreaterThan120(int score) {
        return score >= 50 && score <= 70 || score >= 120;
    }

    private void enableWeaponUpgrade(GraphicsContext gc) {
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
}