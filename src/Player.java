import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * The player.
 */
public class Player extends Rocket {
    int windowWidth, score, scoreAmplifier, ammunitionMax, ammunitionCurrent, reloadTimeMax, reloadTimeCurrent;
    boolean reloading;
    String origin;

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
    public Player(double posX, double posY, int height, int width, double velocity, Image img, int health, int windowWidth, int scoreAmplifier, int damage) {
        super(posX, posY, height, width, velocity, img, health, damage);
        this.windowWidth = windowWidth;
        this.cooldown = 200;
        this.cooldownTracker = 0;
        this.ammunitionMax = 10;
        this.ammunitionCurrent = 10;
        this.reloadTimeMax = 215*7;
        this.reloadTimeCurrent = 0;
        this.reloading = false;
        this.origin = "player";
        this.score = 0;
        this.scoreAmplifier = scoreAmplifier;
    }

    /**
     * Updates the score for the player with regards to the scoreAmplifier value.
     */
    public void updateScore() {
        this.score += 50 * scoreAmplifier;
    }

    /**
     * Get's the players score.
     * @return player score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Draws the player onto the canvas.
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
     * Updates the Player.
     */
    public void update() {
        if (cooldownTracker > 0) cooldownTracker -= 7;       // Update frequency
        if (reloading) {
            reloadTimeCurrent -= 7;
            if (reloadTimeCurrent <= 0) {
                reloading = false;
                ammunitionCurrent = ammunitionMax;
            }
        }
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
        if (cooldownTracker <= 0 && ammunitionCurrent > 0 && !reloading) {
            cooldownTracker = cooldown;
            ammunitionCurrent--;
            if (ammunitionCurrent <= 0) {
                reload();
            }

            Shot[] s = getShots();
            return s;
        } else {
            return null;
        }
    }

    private Shot[] getShots() {
        int amountOfBullets = 1;
        Shot[] s = new Shot[amountOfBullets];
        int bulletVelocity = - 4;
        int h = 16;
        int w = 2;
        double x = posX + width / 2 - w / 2;
        double y = posY - h + 6;
        for (int i = 0; i < amountOfBullets; i++) {
            s[i] = new Shot(x, y, origin, h, w, bulletVelocity, damage);
        }
        return s;
    }

    /**
     * What to do when the player is damaged.
     */
    public void hit(Shot shot) {
        if (healthCurrent - shot.damage > 0) {
            healthCurrent -= shot.damage;
        } else {
            healthCurrent = 0;
            explode();
        }
    }

    public void reload() {
        reloading = true;
        reloadTimeCurrent = reloadTimeMax;
    }
}