package invaders.score;

import invaders.engine.GameWindow;
import invaders.rendering.Renderable;
import javafx.application.Platform;

import javax.swing.*;

public class ScoreUpdateFacade {
    public int scoreValue(Renderable renderableA, Renderable renderableB) {
        if (renderableB.getRenderableObjectName().equals("PlayerProjectile")){
            if (renderableA.getRenderableObjectName().equals("Bunker")) {
                return 0;
            }
            if (renderableA.getRenderableObjectName().equals("slow_projectile")) {
                return 1;
            }
            if (renderableA.getRenderableObjectName().equals("fast_projectile")) {
                return 2;
            }
            if (renderableA.getRenderableObjectName().equals("slow_alien")) {
                return 3;
            }
            if (renderableA.getRenderableObjectName().equals("fast_alien")) {
                return 4;
            }

        }

    return 0;
    }

    public int updateScore(Renderable renderableA, Renderable renderableB, int score) {
        score += scoreValue(renderableA, renderableB);
        return score;
    }
    public int updateScore(int score) {
        return score;
    }

    public void showScore(GameWindow window, int score) {
        Platform.runLater(() ->
                window.getScoreLabel().setText(String.format("Score: %d", score))
        );
    }

}
