package invaders.hud;

import invaders.engine.GameWindow;

public interface HudListener {
    void update(String event, Object data, GameWindow window);
}
