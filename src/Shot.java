import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Bullets (shots) from either the player or enemies.
 */
public class Shot extends Sprite {
    String origin; // ??
    Color color;
    static final int size = 8;
    public boolean toRemove;

    public Shot(int posX, int posY, int height, int width, int velocity) { // String origin?
        super(posX, posY, height, width, velocity);
        this.origin = origin;
        this.color = Color.RED;
    }

    public void update() {
        posY -= velocity;
    }

    public void draw(GraphicsContext gc, int score) {
        gc.setFill(Color.RED);
        if (score >=50 && score<=70 || score>=120) {
            gc.setFill(Color.YELLOWGREEN);
            velocity = 50;
            gc.fillRect(posX-5, posY-10, size+10, size+30);
        } else {
            gc.fillOval(posX, posY, size, size);
        }
    }

    public static int getSize() {
        return size;
    }
}