import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Enemies travelling towards the player.
 */
public class Enemy extends Rocket {
    boolean ableToShoot, boss;
    String origin;
    double shootingProbability;
    Color bgColor, hpColor;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param velocity Speed.
     * @param img Image.
     * @param health Health.
     * @param ableToShoot Is able to shoot or not.
     * @param isBoss Is boss or not.
     */
    public Enemy(double posX, double posY, double velocity, Image img, int health, boolean ableToShoot, boolean isBoss, double shootingProbability, int damage, Color bgColor) {
        super(posX, posY, (int) img.getHeight(), (int) img.getWidth(), velocity, img, health, damage);
        this.ableToShoot = ableToShoot;
        this.cooldown = 200;
        this.cooldownTracker = 0;
        this.origin = "enemy";
        this.shootingProbability = shootingProbability;
        this.bgColor = bgColor;
        this.hpColor = Color.rgb(235, 73, 52);
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
            gc.setFill(hpColor);
            gc.fillRect(posX, posY-4-4, width, 4);
            gc.setFill(bgColor);
            gc.fillRect(posX+1, posY-4-4+1, width-2, 4-2);
            gc.setFill(hpColor);
            gc.fillRect(posX+1, posY-4-4+1, (width-2)*healthCurrent/healthMax, 4-2);
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
                s[i] = new Shot(x, y, origin, h, w, bulletVelocity, damage);
            }
            return s;
        } else {
            return null;
        }
    }

    /**
     * What to do when the enemy is damaged.
     */
    public void hit(Shot shot) {
        if (healthCurrent - shot.damage > 0) {
            healthCurrent -= shot.damage;
        } else {
            healthCurrent = 0;
            explode();
        }
    }
}