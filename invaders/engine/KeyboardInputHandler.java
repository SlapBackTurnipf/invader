package invaders.engine;

import invaders.Difficulty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import invaders.App;

public class KeyboardInputHandler {
    private final GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();

    private Map<String, MediaPlayer> sounds = new HashMap<>();

    private static KeyboardInputHandler instance;


    KeyboardInputHandler(GameEngine model, Difficulty difficulty) {
        this.model = model;

        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }
    public static KeyboardInputHandler getInstance(GameEngine model, Difficulty difficulty){
        if (!App.KeyboardInputHandlers.containsKey(difficulty)){
            instance = new KeyboardInputHandler(model, difficulty);
            instance.resetKeyState();
            App.KeyboardInputHandlers.put(difficulty, instance);

        }

        if (App.KeyboardInputHandlers.containsKey(difficulty)) {
            instance = App.KeyboardInputHandlers.get(difficulty);
            instance.resetKeyState();
        }
        return instance;
    }
    void resetKeyState() {
        pressedKeys.clear();
        left = false;
        right = false;
        model.setPaused(true);
    }
    void handlePressed(KeyEvent keyEvent) {
        if (isCheatKeyPressed() && !pressedKeys.contains(keyEvent.getCode())) {
            return; // Ignore if another cheat key is already pressed
        }
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (!model.getIsPaused()) {
            if (keyEvent.getCode().equals(KeyCode.SPACE)) {

                if (model.shootPressed()) {
//                    model.saveStateToMemento();
                    MediaPlayer shoot = sounds.get("shoot");
                    shoot.stop();
                    shoot.play();
                }
            }
        }
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
            if (!model.getIsPaused()) { model.togglePause(); }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }
        if (keyEvent.getCode().equals((KeyCode.L))){
            model.restoreStateFromMemento();
            //the aliens are not moving after this is called because the timeline is not being restarted
            //this is the code that restarts the timeline:
        }
        if (keyEvent.getCode().equals((KeyCode.S))){
            model.saveStateToMemento();
        }
        if (keyEvent.getCode() == KeyCode.T) {
            model.removeFastProjectiles();
        }
        if (keyEvent.getCode() == KeyCode.Y) {
            model.removeSlowProjectiles();
        }
        if (keyEvent.getCode() == KeyCode.U) {
            model.removeFastAliens();
        }
        if (keyEvent.getCode() == KeyCode.I) {
            model.removeSlowAliens();
        }

        if (left) {
            model.leftPressed();
        }

        if(right){
            model.rightPressed();
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }
    }

    private boolean isCheatKeyPressed() {
        return pressedKeys.contains(KeyCode.T) || pressedKeys.contains(KeyCode.Y) ||
                pressedKeys.contains(KeyCode.U) || pressedKeys.contains(KeyCode.I);
    }
}
