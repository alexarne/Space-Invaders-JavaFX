
/**
 * Any substantial object which is drawn: Player, Enemies, Bullets, Stars, etc..
 */
public class Sprite {
    int posX, posY, height, width, velocity;
    boolean dead;

    public Sprite(int posX, int posY, int height, int width, int velocity) {
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
        int xLeft1 = this.posX;
        int xRight1 = this.posX + this.width;
        int yUp1 = this.posY;
        int yDown1 = this.posY + this.height;
        // The other sprite's restraints:
        int xLeft2 = who.posX;
        int xRight2 = who.posX + who.width;
        int yUp2 = who.posY;
        int yDown2 = who.posY + who.height;

        return (xLeft1 < xRight2) && (xLeft2 < xRight1) && (yUp1 < yDown2) && (yUp2 < yDown1);
    }
}