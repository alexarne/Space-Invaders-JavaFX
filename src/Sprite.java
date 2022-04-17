
/**
 * Any substantial object which is drawn: Player, Enemies, Bullets, Stars, etc..
 */
public class Sprite {
    int height, width;
    double posX, posY, velocity;
    boolean dead;

    /**
     * Constructor.
     * @param posX X-position.
     * @param posY Y-position.
     * @param height Height in y-axis.
     * @param width Width in x-axis.
     * @param velocity Speed.
     */
    public Sprite(double posX, double posY, int height, int width, double velocity) {
        this.posX = posX;
        this.posY = posY;
        this.height = height;
        this.width = width;
        this.velocity = velocity;
        this.dead = false;
    }

    /**
     * Checks if this object and another object has collided. Treats sprites as rectangles.
     * @param who The other object to do the collision check with.
     * @return True if the two objects have collided, false if not.
     */
    public boolean hasCollided(Sprite who) {
        // This Sprite's restraints:
        double xLeft1 = this.posX;
        double xRight1 = this.posX + this.width;
        double yUp1 = this.posY;
        double yDown1 = this.posY + this.height;
        // The other sprite's restraints:
        double xLeft2 = who.posX;
        double xRight2 = who.posX + who.width;
        double yUp2 = who.posY;
        double yDown2 = who.posY + who.height;

        return (xLeft1 < xRight2) && (xLeft2 < xRight1) && (yUp1 < yDown2) && (yUp2 < yDown1);
    }
}