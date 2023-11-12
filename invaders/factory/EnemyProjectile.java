package invaders.factory;

import invaders.engine.GameEngine;
import invaders.physics.Collider;
import invaders.physics.Vector2D;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.ProjectileStrategy;
import javafx.scene.image.Image;

public class EnemyProjectile extends Projectile{
    private ProjectileStrategy strategy;

    public EnemyProjectile(Vector2D position, ProjectileStrategy strategy, Image image) {
        super(position,image);
        this.strategy = strategy;
    }

    @Override
    public void update(GameEngine model) {
        strategy.update(this);

        if(this.getPosition().getY()>= model.getGameHeight() - this.getImage().getHeight()){
            this.takeDamage(1);
        }

    }
    @Override
    public String getRenderableObjectName() {
        if (getStrategy() instanceof FastProjectileStrategy) {
            return "fast_projectile";
        } else {
            return "slow_projectile";
        }
    //not needed but implemented for readability
    }
    public ProjectileStrategy getStrategy(){
        return this.strategy;
    }
}
