import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    static final Image PLAYER_IMG = new Image("Assets/Images/rocketclean64.png");
    Stage window;
    Scene mainScene, gameScene;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        window = stage;

        Image icon = new Image("Assets/Images/rocketoutline.png");
        window.getIcons().add(icon);
        window.setTitle("Space Invaders");

        mainMenu();
        game();

        window.setWidth(600);
        window.setHeight(900);
        window.setResizable(false);

        window.setScene(mainScene);
        window.show();
    }

    public void mainMenu() {
        Group root = new Group();
        mainScene = new Scene(root, Color.WHITE);

        Text menuText = new Text();
        menuText.setText("Main Menu");
        menuText.setFill(Color.BLACK);
        menuText.setX(50);
        menuText.setY(50);
        menuText.setFont(Font.font("Sitka Small", 40));

        Text startGame = new Text();
        startGame.setText("Start Game");
        startGame.setFill(Color.BLACK);
        startGame.setX(50);
        startGame.setY(150);
        startGame.setFont(Font.font("Sitka Small", 20));

        startGame.setOnMouseEntered(mouseEvent -> startGame.setFill(Color.GREY));
        startGame.setOnMouseExited(mouseEvent -> startGame.setFill(Color.BLACK));
        startGame.setOnMouseReleased(mouseEvent -> window.setScene(gameScene));

        root.getChildren().add(menuText);
        root.getChildren().add(startGame);
    }

    public void game() {
        Group root = new Group();
        gameScene = new Scene(root, Color.BLACK);

        Text text = new Text();
        text.setText("lola issa game");
        text.setFill(Color.WHITE);
        text.setX(250);
        text.setY(350);
        text.setFont(Font.font("Sitka Small", 20));

        Rectangle rectangle = new Rectangle(50, 50, 64, 64);
        rectangle.setFill(Color.BLUE);

        root.getChildren().add(rectangle);
        root.getChildren().add(text);
    }

    // Player
    public class Player {
        int posX, posY, size, explosionStep;
        boolean exploding, dead;
        Image img;

        // Constructor
        public Player(int posX, int posY, int size, Image image) {
            this.posX = posX;
            this.posY = posY;
            this.size = size;
            this.img = image;
            explosionStep = 0;
        }
    }
}

//    @Override
//    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
//    }