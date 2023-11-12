package invaders.memento;

import invaders.App;
import invaders.engine.GameEngine;
import invaders.factory.EnemyProjectile;
import invaders.gameobject.Enemy;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.ProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameMemento {
    private List<Renderable> renderablesSnapshot;
    private int scoreSnapshot;
    private float timeSnapshot;

    public GameMemento(List<Renderable> renderables, int score, float time) {
        this.renderablesSnapshot = deepCopyRenderables(renderables);
        this.scoreSnapshot = score;
        this.timeSnapshot = time;
    }

    public void restoreState(GameEngine gameEngine) {
        gameEngine.addAllRenderablesAndGameObjects(renderablesSnapshot);
        gameEngine.setScore(scoreSnapshot);
        gameEngine.setTime(timeSnapshot);
        App.getWindow().updateTimer((int) timeSnapshot);
    }

    private List<Renderable> deepCopyRenderables(List<Renderable> originalRenderables) {
        return originalRenderables.stream()
                .map(this::createDeepCopyRenderable)
                .collect(Collectors.toList());
    }

    private Renderable createDeepCopyRenderable(Renderable original) {
        if (original instanceof Enemy) {
            return deepCopyEnemy((Enemy) original);
        } else if (original instanceof EnemyProjectile) {
            return deepCopyProjectile((EnemyProjectile) original);
        }
        return original;
    }

    private Enemy deepCopyEnemy(Enemy enemy) {
        Enemy enemyCopy = new Enemy(new Vector2D(enemy.getPosition().getX(), enemy.getPosition().getY()));
        copyEnemyAttributes(enemy, enemyCopy);
        return enemyCopy;
    }

    private void copyEnemyAttributes(Enemy original, Enemy copy) {
        copy.setLives((int) original.getHealth());
        copy.setImage(original.getImage());
        copy.setProjectileImage(original.getProjectileImage());
        copy.setxVel(original.getxVel());
        copy.setProjectileStrategy(copyProjectileStrategy(original.getProjectileStrategy()));
    }

    private EnemyProjectile deepCopyProjectile(EnemyProjectile projectile) {
        Vector2D positionCopy = new Vector2D(projectile.getPosition().getX(), projectile.getPosition().getY());
        EnemyProjectile projectileCopy = new EnemyProjectile(positionCopy,
                copyProjectileStrategy(projectile.getStrategy()),
                projectile.getImage());
        return projectileCopy;
    }

    private ProjectileStrategy copyProjectileStrategy(ProjectileStrategy strategy) {
        if (strategy instanceof FastProjectileStrategy) {
            return new FastProjectileStrategy();
        } else if (strategy instanceof SlowProjectileStrategy) {
            return new SlowProjectileStrategy();
        }
        return strategy;
    }

    public List<Renderable> getRenderablesSnapshot() {
        return renderablesSnapshot;
    }

}