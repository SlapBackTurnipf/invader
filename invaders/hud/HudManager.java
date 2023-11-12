package invaders.hud;

import invaders.engine.GameWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class HudManager {
    Map<String, List<HudListener>> listeners = new HashMap<>();

    public HudManager(String... events) {
        for (String event : events) {
            this.listeners.put(event, new ArrayList<>());
        }
    }
    public void subscribe(String event, HudListener listener) {
        List<HudListener> users = listeners.get(event);
        users.add(listener);
    }

    public void unsubscribe(String event, HudListener listener) {
        List<HudListener> users = listeners.get(event);
        users.remove(listener);
    }

    public void notify(String event, Object data, GameWindow window) {
        List<HudListener> users = listeners.get(event);
        for (HudListener listener : users) {
            listener.update(event, data,window);
        }
    }




}
