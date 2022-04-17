
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.Random;

public class LevelLoader {
    String filepath;
    Enemy[] enemies;
    int WINDOW_WIDTH, amountOfEnemies;
    Color gameBgColor;
    Random rnd;

    public LevelLoader(int WINDOW_WIDTH, Color gameBgColor, Random rnd) {
        this.filepath = "Assets/Levels/game_levels.txt";
        this.WINDOW_WIDTH = WINDOW_WIDTH;
        this.gameBgColor = gameBgColor;
        this.rnd = rnd;
    }

    /**
     * Get the enemy-array to load by the level, given a particular level.
     * @param num The number of the level.
     * @return The enemy-array for that specified level.
     */
    public Enemy[] getEnemies(int num) {
        // Read text file and assign values accordingly //TODO
        BufferedReader levelsReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filepath)));

        try {
            boolean reading = true;
            boolean foundLevel = false;
            boolean levelConfigured = false;
            int enemiesAdded = 0;

            String data;
            while ((data = levelsReader.readLine()) != null && reading) {
                if (!foundLevel) {
                    if (data.equalsIgnoreCase("level " + num)) {
                        foundLevel = true;
                        amountOfEnemies = Integer.parseInt(levelsReader.readLine());
                        enemies = new Enemy[amountOfEnemies];
                    }
                } else {
                    if (data.startsWith("enemy")) {
                        if (enemiesAdded >= amountOfEnemies) {
                            System.out.println("ERROR: More enemies attempted to load than specified.");
                            break;
                        }
                        String[] arr = data.split("\\s+");
                        Image img = new Image("Assets/Images/enemyrocket" + arr[1] + ".png");
                        double x = rnd.nextInt(WINDOW_WIDTH - (int) img.getWidth());
                        double y = - (int) img.getHeight();
                        double v = Double.parseDouble(arr[2]);
                        int hp = Integer.parseInt(arr[3]);
                        boolean s = Boolean.parseBoolean(arr[4]);
                        boolean b = Boolean.parseBoolean(arr[5]);
                        double sP = Double.parseDouble(arr[6]);
                        int dmg = Integer.parseInt(arr[7]);
                        enemies[enemiesAdded] = new Enemy(x, y, v, img, hp, s, b, sP, dmg, gameBgColor);
                        enemiesAdded++;
                    } else if (data.startsWith("-")) {
                        levelConfigured = true;
                    } else {
                        System.out.println("Line not recognized:\n" + data);
                    }
                }
                if (foundLevel && levelConfigured) {
                    System.out.println("Level " + num + " found and configured.");
                    reading = false;
                }
            }
            if (!foundLevel) System.out.println("ERROR: Level not found.");
            if (foundLevel && !levelConfigured) System.out.println("ERROR: Level found but not fully configured.");
            if (enemiesAdded < amountOfEnemies) System.out.println("ERROR: Fewer enemies than specified were loaded.");
            levelsReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enemies;
    }

    /**
     * Gets the amount of enemies for the loaded level, cannot be called before loading level.
     * @return The amount of enemies.
     */
    public int getAmountOfEnemies() {
        return amountOfEnemies;
    }

    /**
     * Looks through the file and counts the occurences of string "level" to determine how many levels are specified.
     * @return The amount of levels.
     */
    public int getAmountOfLevels() {
        BufferedReader levelsReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filepath)));

        int counter = 0;
        try {
            String data;
            while ((data = levelsReader.readLine()) != null) {
                if (data.startsWith("level ")) counter++;
            }
            levelsReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }
}
