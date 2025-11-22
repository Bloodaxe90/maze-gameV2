package io.github.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;

import io.github.game.Game;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public abstract class EntityManager {

    protected final TextureAtlas spriteAtlas;
    protected Map<String, Entity> entities = new HashMap<>();

    public EntityManager(TextureAtlas spriteAtlas) {
        this.spriteAtlas = spriteAtlas;
    }

    public void render(SpriteBatch batch) {
        if (!entities.isEmpty()) {
            for (Entity entity : entities.values()) {
                entity.render(batch);
            }
        }
    }


    public void update(float delta_t, Game game) {
        Iterator<Entity> iterator = entities.values().iterator();

        while (iterator.hasNext()) {
            Entity entity = iterator.next();

            entity.update(delta_t, game);

            if (!entity.isActive()) {
                iterator.remove();
            }
        }
    }


    public boolean checkCollision(Rectangle hitbox) {
        if (!entities.isEmpty()) {
            for (Entity entity : entities.values()) {
                if (entity.isCollidable())
                    if (entity.getHitbox().overlaps(hitbox)) {
                        entity.setTriggered(true);
                        return true;
                    }
            }
        }
        return false;
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public void dispose() {
        spriteAtlas.dispose();
    }
}
