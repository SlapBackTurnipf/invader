package invaders.difficulty;

import invaders.App;
import invaders.engine.GameEngine;
import invaders.engine.GameWindow;

public class EasyGameInstance {
    private static EasyGameInstance instance;
    private GameEngine model;

    private EasyGameInstance(String configPath, App app) {
        model = new GameEngine(configPath, app);
    }

    public static EasyGameInstance getInstance(String configPath, App app) {
        if (instance == null) {
            instance = new EasyGameInstance(configPath, app);
        }
        return instance;
    }

    public GameEngine getModel() {
        return model;
    }
}


