import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

public class LevelLoader {
    Enemy[] enemies;
    int WINDOW_WIDTH, amountOfEnemies;
    Color gameBgColor;
    Random rnd;

    public LevelLoader(int WINDOW_WIDTH, Color gameBgColor, Random rnd) {
        this.WINDOW_WIDTH = WINDOW_WIDTH;
        this.gameBgColor = gameBgColor;
        this.rnd = rnd;
    }

    public Enemy[] getEnemies(int num) {
        // Read text file and assign values accordingly //TODO

        amountOfEnemies = 20;
        enemies = new Enemy[amountOfEnemies];
        for (int i = 0; i < amountOfEnemies; i++) {
//            int n = 1 + rnd.nextInt(3);
            Image img = new Image("Assets/Images/enemyrocket" + 1 + ".png");
            double x = rnd.nextInt(WINDOW_WIDTH - (int) img.getWidth());
            double y = - (int) img.getHeight();
            double v = 1;
            int hp = 100;
            boolean s = true;
            boolean b = false;
            int dE = 30;
            enemies[i] = new Enemy(x, y, v, img, hp, s, b, 0.003, dE, gameBgColor);
        }
        return enemies;
    }

    public int getAmountOfEnemies() {
        return amountOfEnemies;
    }
}
