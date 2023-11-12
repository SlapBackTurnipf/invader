package invaders;

import invaders.difficulty.EasyGameInstance;
import invaders.difficulty.HardGameInstance;
import invaders.difficulty.MediumGameInstance;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import invaders.engine.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class App extends Application {

    private Stage primaryStage;
    private static GameWindow window;
    public static GameEngine model;
    private boolean isPaused = false;
    public static Difficulty currentDifficulty;
    public static Map<Difficulty, GameWindow> windows = new HashMap<>();
    public static Map<Difficulty, KeyboardInputHandler> KeyboardInputHandlers = new HashMap<>();
    public static String config;
    public static App app;

//YOU HAVE TO CREATE A DEEP COPY OF THE APP CLASS AND THEN PUSH IT ONTO THE STACK
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox root = new VBox(10);
        app = this;
        // Setup the main menu
        setupMainMenu();

        // Configure the primary stage
        primaryStage.setTitle("Space Invaders");
        primaryStage.show();

        primaryStage.setResizable(true);


    }

    public void setupMainMenu() {
        // Create buttons for each difficulty level
        Button easyButton = new Button("Easy");
        easyButton.setOnAction(e -> startGame("src/main/resources/config_easy.json"));
        this.config = "src/main/resources/config_easy.json";

        Button mediumButton = new Button("Medium");
        mediumButton.setOnAction(e -> startGame("src/main/resources/config_medium.json"));
        this.config = "src/main/resources/config_medium.json";

        Button hardButton = new Button("Hard");
        hardButton.setOnAction(e -> startGame("src/main/resources/config_hard.json"));
        this.config = "src/main/resources/config_hard.json";

        // Layout for buttons
        VBox buttonLayout = new VBox(10); // 10 is the spacing between buttons
        buttonLayout.setAlignment(Pos.CENTER); // Center alignment for buttons
        buttonLayout.getChildren().addAll(easyButton, mediumButton, hardButton);

        StackPane layout = new StackPane();
        layout.getChildren().add(buttonLayout);
        layout.setAlignment(Pos.CENTER); // Ensure the VBox is centered in the StackPane

        Scene mainMenuScene = new Scene(layout, 300, 250);
        primaryStage.setScene(mainMenuScene);
    }

    public void startGame(String configPath) {

        if (configPath.contains("easy")) {
            currentDifficulty = Difficulty.EASY;
            model = EasyGameInstance.getInstance(configPath, this).getModel();

        } else if (configPath.contains("medium")) {
            currentDifficulty = Difficulty.MEDIUM;
            model = MediumGameInstance.getInstance(configPath, this).getModel();

        } else if (configPath.contains("hard")) {
            currentDifficulty = Difficulty.HARD;
            model = HardGameInstance.getInstance(configPath, this).getModel();

        }

        window = GameWindow.getInstance(model, currentDifficulty);
        primaryStage.setScene(window.getScene());



        window.startGame();

        window.getScene().getRoot().requestFocus();

        //to make this centered on the screen this is the code:

        primaryStage.setResizable(true);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);

        //this code makes it so that when the escape key is pressed, the game will create a pause menu:

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public static GameEngine getModel() { return model; }
    public static GameWindow getWindow() {
        return window;
    }
    public static String getConfig() { return config; }
    public static App getApp() { return app; }
    public static Difficulty getCurrentDifficulty() { return currentDifficulty; }

//    public void restoreGame() {
//        Caretaker caretaker = new Caretaker();
//        mementoStack = caretaker.getSavedStates();
//
//    }
//    public App createSnapshot() {
//        App mementoApp = new App();
//        mementoApp.primaryStage = this.primaryStage;
//        mementoApp.window = this.window;
//        mementoApp.model = this.model;
//        mementoApp.isPaused = this.isPaused;
//        mementoApp.currentDifficulty = this.currentDifficulty;
//        mementoApp.windows = this.windows;
//        mementoApp.KeyboardInputHandlers = this.KeyboardInputHandlers;
//        mementoApp.config = this.config;
//        mementoApp.app = this.app;
//        return mementoApp;
//    }



}

