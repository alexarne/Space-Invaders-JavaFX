import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Enemy extends Rocket {
    boolean ableToShoot, boss;
    String origin;
    double shootingProbability;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param velocity Speed.
     * @param img Image.
     * @param health Health.
     * @param ableToShoot Is able to shoot or not.
     * @param boss Is boss or not.
     */
    public Enemy(double posX, double posY, double velocity, Image img, int health, boolean ableToShoot, boolean boss, double shootingProbability) {
        super(posX, posY, (int) img.getHeight(), (int) img.getWidth(), velocity, img, health);
        this.ableToShoot = ableToShoot;
        this.cooldown = 200;
        this.cooldownTracker = 0;
        this.origin = "enemy";
        this.shootingProbability = shootingProbability;
    }

    /**
     * Draws the enemy onto the canvas.
     * @param gc The canvas.
     */
    public void draw(GraphicsContext gc) {
        if (exploding) {
            this.drawExplode(gc);
        } else {
            update();
            gc.drawImage(img, posX, posY);
        }
    }

    /**
     * Updates the Enemy.
     */
    public void update() {
        this.posY += velocity;
        if (cooldownTracker > 0) cooldownTracker -= 7;
    }

    /**
     * Lets an enemy shoot.
     * @return An array of bullets if allowed, null if still on cooldown.
     */
    public Shot[] shoot() {
        if (cooldownTracker <= 0) {
            cooldownTracker = cooldown;
            int amountOfBullets = 1;
            Shot[] s = new Shot[amountOfBullets];
            int bulletVelocity = - 4;
            int h = 16;
            int w = 2;
            double x = posX + width / 2 - w / 2;
            double y = posY + height - h;
            for (int i = 0; i < amountOfBullets; i++) {
                s[i] = new Shot(x, y, h, w, bulletVelocity, origin);
            }
            return s;
        } else {
            return null;
        }
    }
}