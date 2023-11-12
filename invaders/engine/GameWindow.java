package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.App;
import invaders.Difficulty;
import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameWindow {
	private final int width;
    private final int height;
	private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews =  new ArrayList<EntityView>();
    private Renderable background;
    private Timeline timeline;
    private int secondsPassed = 0;
    private Label timerLabel;
    private Label scoreLabel;

    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;

    private static GameWindow instance;
    private Difficulty currentDifficulty;



	public GameWindow(GameEngine model, Difficulty difficulty) {
        this.model = model;
		this.width =  model.getGameWidth();
        this.height = model.getGameHeight();

        timerLabel = new Label("00:00");
        timerLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        timerLabel.setStyle("-fx-font-size: 2em;");
        timerLabel.centerShapeProperty();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        scoreLabel = new Label("Score: " + model.getScore());
        scoreLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        scoreLabel.setStyle("-fx-font-size: 2em;");
        scoreLabel.centerShapeProperty();
        //how to make scoreLabel appear in the top right of the screen
        scoreLabel.setAlignment(Pos.TOP_RIGHT);
        scoreLabel.setTranslateX(100);


        pane = new Pane();
        scene = new Scene(pane, width, height);
        pane.getChildren().add(timerLabel);
        pane.getChildren().add(scoreLabel);

        this.background = new SpaceBackground(model, pane);

//        KeyboardInputHandler keyboardInputHandler = KeyboardInputHandler.getInstance(model);
        KeyboardInputHandler keyboardInputHandler = KeyboardInputHandler.getInstance(model, difficulty);
        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

    }
    public int getSecondsPassed() {
        return secondsPassed;
    }

    private void updateTimer() {
        secondsPassed++;
        int minutes = secondsPassed / 60;
        int seconds = secondsPassed % 60;
        Platform.runLater(() ->
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds))
        );

    }
    public void updateTimer(int secondsPassed) {
        this.secondsPassed = secondsPassed;
        int minutes = secondsPassed / 60;
        int seconds = secondsPassed % 60;
        Platform.runLater(() ->
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds))
        );

    }

    public Label getScoreLabel() { return scoreLabel; }
    public void startGame() {
        timeline.play();
    }

    public void pauseGame() {
        timeline.pause();
    }


    public static GameWindow getInstance(GameEngine model, Difficulty difficulty){
        if (!App.windows.containsKey(difficulty)){
            instance = new GameWindow(model, difficulty);
            App.windows.put(difficulty, instance);
            instance.run();

        }
        if (App.windows.containsKey(difficulty)){
            instance = App.windows.get(difficulty);
        }
        model.resume();
        model.leftReleased();
        model.rightReleased();
        return instance;
    }


	public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> {
              model.update();
              this.draw();
         }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void draw(){

        List<Renderable> renderables = model.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }


        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);

    }
    public void removeAllEntitiesFromScreen() {
        for (EntityView entityView : entityViews) {
            pane.getChildren().remove(entityView.getNode());
        }
        entityViews.clear();
    }



	public Scene getScene() {
        return scene;
    }

    public float getTime() {
        return secondsPassed;
    }


}

//in this program, the code that shows the differing speeds of the different enemies is in the Enemy class. More specifically the method called


