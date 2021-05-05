import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * The player.
 */
public class Player extends Rocket {
    int windowWidth;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param height Height in y-axis.
     * @param width Width in x-axis.
     * @param velocity Speed.
     * @param img Image.
     * @param health Health.
     * @param windowWidth The screens width.
     */
    public Player(double posX, double posY, int height, int width, double velocity, Image img, int health, int windowWidth) {
        super(posX, posY, height, width, velocity, img, health);
        this.windowWidth = windowWidth;
        this.cooldown = 200;
        this.cooldownTracker = 0;
    }

    /**
     * Draws the player onto the canvas.
     * @param gc The canvas.
     */
    public void draw(GraphicsContext gc) {
        update();
        gc.drawImage(img, posX, posY);
    }

    /**
     * Updates the Player.
     */
    public void update() {
        if (cooldownTracker > 0) cooldownTracker -= 7;       // Update frequency
    }

    /**
     * Move the player to the left with respect to its velocity.
     */
    public void moveLeft() {
        if (posX <= velocity) {
            this.posX = 0;
        } else {
            this.posX -= velocity;
        }
    }

    /**
     * Move the player to the right with respect to its velocity.
     */
    public void moveRight() {
        if (posX + velocity > windowWidth - this.width) {
            this.posX = windowWidth - this.width;
        } else {
            this.posX += velocity;
        }
    }

    /**
     * Lets the player shoot.
     * @return An array of bullets if allowed, null if still on cooldown.
     */
    public Shot[] shoot() {
        if (cooldownTracker <= 0) {
            cooldownTracker = cooldown;
            int amountOfBullets = 1;
            Shot[] s = new Shot[amountOfBullets];
            int bulletVelocity = - 4;
            int h = Shot.size*2;
            int w = 2;
            double x = posX + width / 2 - w / 2;
            double y = posY - h + 6;
            for (int i = 0; i < amountOfBullets; i++) {
                s[i] = new Shot(x, y, h, w, bulletVelocity);
            }
            return s;
        } else {
            return null;
        }
    }
}