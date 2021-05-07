import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Enemies travelling towards the player.
 */
public class Rocket extends Sprite {
    int explosionStep, explosionStepMax, EXPLOSION_COL, EXPLOSION_ROW,
            EXPLOSION_W, EXPLOSION_H, EXPLOSION_TIMER, health, cooldown,
            cooldownTracker, damage;
    boolean exploding;
    Image img, EXPLOSION_IMG;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param height Height in y-axis.
     * @param width Width in x-axis.
     * @param velocity Speed.
     * @param img Image.
     * @param health Health.
     */
    public Rocket(double posX, double posY, int height, int width, double velocity, Image img, int health, int damage) {
        super(posX, posY, height, width, velocity);
        this.img = img;
        this.health = health;
        this.explosionStep = 0;
        this.explosionStepMax = 13;
        this.exploding = false;
        this.EXPLOSION_IMG = new Image("Assets/Images/explosion.png");
        this.EXPLOSION_COL = 4;
        this.EXPLOSION_ROW = 4;
        this.EXPLOSION_W = (int) EXPLOSION_IMG.getWidth() / EXPLOSION_COL;
        this.EXPLOSION_H = (int) EXPLOSION_IMG.getHeight() / EXPLOSION_ROW;
        this.EXPLOSION_TIMER = 0;
        this.damage = damage;
    }

    public void explode() {
        exploding = true;
        explosionStep = -1;
    }

    public void drawExplode(GraphicsContext gc) {
        if (explosionStep > explosionStepMax) {
            this.dead = true;
        } else {
            EXPLOSION_TIMER++;
            if (EXPLOSION_TIMER % 8 == 0) {
                explosionStep++;
            }
            gc.drawImage(EXPLOSION_IMG, (explosionStep % EXPLOSION_COL)*EXPLOSION_W, (explosionStep / EXPLOSION_ROW)*EXPLOSION_H+1,
                    EXPLOSION_W, EXPLOSION_H, this.posX, this.posY, this.width, this.width);
        }
    }
}