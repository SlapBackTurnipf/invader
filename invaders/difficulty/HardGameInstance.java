package invaders.difficulty;

import invaders.App;
import invaders.engine.GameEngine;
public class HardGameInstance {
    private static HardGameInstance instance;
    private GameEngine model;

    private HardGameInstance(String configPath, App app) {
        model = new GameEngine(configPath, app);
    }

    public static HardGameInstance getInstance(String configPath, App app) {
        if (instance == null) {
            instance = new HardGameInstance(configPath, app);
        }
        return instance;
    }

    public GameEngine getModel() {
        return model;
    }
}