package io.github.game.entity.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

import io.github.game.Game;
import io.github.game.entity.MovableEntity;


public class Enemy extends MovableEntity {

    private final float range;

    public Enemy(RectangleMapObject properties,
                 TextureAtlas spriteAtlas) {

        super(properties, spriteAtlas);

        this.range = getStartingProperty("range", Float.class);
    }

    @Override
    public void update(float delta_t, Game game) {
        super.update(delta_t, game);

        if (!active) return;

        Vector2 velocity = new Vector2(0f, 0f);

                Vector2 dir = game.getEntityManager().getPlayer().getPos().sub(this.position);

                float distanceToPlayer = (float) Math.sqrt(dir.x * dir.x + dir.y * dir.y);

                if (distanceToPlayer <= range * game.getEnvironmentManager().getTileSize() || range < 0) {
            velocity.x = (dir.x / distanceToPlayer) * this.speed;
            velocity.y = (dir.y / distanceToPlayer) * this.speed;
        } else {
            velocity = new Vector2(0f, 0f);
        }

        float newX = position.x + (velocity.x * delta_t);
        float newY = position.y + (velocity.y * delta_t) ;

        if (!isCollidable()) {
            setXPos(newX);
            setYPos(newY);
            return;
        }

        float oldX = position.x;
        float oldY = position.y;

        setXPos(newX);
        if (game.getEnvironmentManager().checkCollision(this)) {
            setXPos(oldX);
        }
        setYPos(newY);
        if (game.getEnvironmentManager().checkCollision(this)) {
           setYPos(oldY);
        }

        updateSprite(velocity);
    }
}
