package invaders.difficulty;

import invaders.App;
import invaders.engine.GameEngine;
public class MediumGameInstance {
    private static MediumGameInstance instance;
    private GameEngine model;

    private MediumGameInstance(String configPath, App app) {
        model = new GameEngine(configPath, app);
    }

    public static MediumGameInstance getInstance(String configPath, App app) {
        if (instance == null) {
            instance = new MediumGameInstance(configPath, app);
        }
        return instance;
    }

    public GameEngine getModel() {
        return model;
    }
}
