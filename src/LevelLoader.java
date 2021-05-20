
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class LevelLoader {
    String filename;
    Enemy[] enemies;
    int WINDOW_WIDTH, amountOfEnemies, amountOfLevels;
    Color gameBgColor;
    Random rnd;
	File file;

    public LevelLoader(int WINDOW_WIDTH, Color gameBgColor, Random rnd) {
        this.filename = "src/Assets/Levels/game_levels.txt";
        this.WINDOW_WIDTH = WINDOW_WIDTH;
        this.gameBgColor = gameBgColor;
        this.rnd = rnd;
		try {
			file = new File(getClass().getResource("Assets/Levels/game_levels.txt").toURI());
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
    }

    /**
     * Get the enemy-array to load by the level, given a particular level.
     * @param num The number of the level.
     * @return The enemy-array for that specified level.
     */
    public Enemy[] getEnemies(int num) {
        // Read text file and assign values accordingly //TODO

        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(file);
            boolean reading = true;
            boolean foundLevel = false;
            boolean levelConfigured = false;
            int enemiesAdded = 0;

            while (myReader.hasNextLine() && reading) {
                String data = myReader.nextLine();
                if (!foundLevel) {
                    if (data.equalsIgnoreCase("level " + num)) {
                        foundLevel = true;
                        amountOfEnemies = Integer.parseInt(myReader.nextLine());
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
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
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
        int counter = 0;
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.startsWith("level ")) counter++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return counter;
    }
}
