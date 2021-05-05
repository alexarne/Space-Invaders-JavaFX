# alexgu-joakimai-project

### Description of the project
We will create Space Invaders using java and JavaFX. The “minimum viable product” will be developed using various tutorials and resources on the internet. Once we have an initial version of the product, we will continue to develop it and put our own spin on it. We will generate ideas for how the game should work (game design), find suitable classes and methods, and implement these in java and JavaFX.

The initial product will include core features such as the ability to move the space ship horizontally, shoot and destroy enemy spaceships, gain points when destroying enemies, weapon upgrades, and losing when colliding with an enemy. Other features we might implement include: progressing to a new level once a certain score has been reached; bosses once the player reaches a certain level; have enemies that move in different patterns; change the look of the GUI and enemies so that the player gets the feeling that he or she is fighting in different solar systems; etc..

The goal is to have a playable, fun, and reasonably complex version of Space Invaders.

### How to run the application

The application has been developed using the JavaFX SDK (version 16, but may work with earlier versions). The IDE we used was IntelliJ and therefore the guides linked will be for IntelliJ, but there are alternatives. This guide is also mostly based off of the official [JavaFX documentation](https://openjfx.io/openjfx-docs/).

1. Install IntelliJ.

2. Download the appropriate [JavaFX SDK](https://gluonhq.com/products/javafx/) for your operating system and unzip it to a desired location.

3. Open the project in IntelliJ.

4. Set the Project SDK to Java version 16 (or anything above or equal to 11). You can do this by clicking `File` -> `Project Structure` -> `Project`.

5. Create a library by going into `File` -> `Project Structure` -> `Libraries` and clicking the plus sign. Select the lib folder which is in the JavaFX SDK folder that you just downloaded and click apply.

6. Lastly, go into `Run` -> `Edit Configurations...` and add the following line as VM options: `--module-path "\path\to\javafx-sdk-15.0.1\lib" --add-modules javafx.controls,javafx.fxml`. You might have to click the dropdown "Modify options" and enable "VM Options" in order for said setting to appear. The value for `--module-path` (`\path\to\javafx-sdk-15.0.1\lib`) should be changed to the path to the exact same lib folder that you previously downloaded.

7. Now you should be able to run the program from within IntelliJ.
