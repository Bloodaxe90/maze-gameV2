package io.github.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import io.github.game.Game;
import io.github.game.entities.player.Player;
import io.github.game.utils.io.MapLoader;
import io.github.game.utils.triggers.Trigger;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EntityManager {

    protected Map<String, Entity> entities = new HashMap<>();

    public EntityManager() {
        Set<Class<? extends Entity>> childClasses = new Reflections("io.github.game.entities").getSubTypesOf(Entity.class);
        for (Class<? extends Entity> childClass : childClasses) {

            String layerName = childClass.getSimpleName();


            for (RectangleMapObject properties : MapLoader.getLayerRectangles(layerName)) {
                try {
                    entities.put(
                        properties.getName(),
                        childClass.getConstructor(RectangleMapObject.class, TextureAtlas.class)
                            .newInstance(properties, new TextureAtlas("assets/atlas/" + layerName + ".atlas"))
                    );
                } catch (Exception e) {
                    Gdx.app.log("ERROR", "Could not create entity " + childClass.getSimpleName() + ": " + e);
                }
            }
        }
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

    public boolean checkCollision(Entity entity1) {
        if (entity1.isCollidable()) {
            if (!entities.isEmpty()) {
                for (Entity entity2 : entities.values()) {
                    if (entity1.isCollidable() && !entity1.equals(entity2))
                        if (entity2.getHitbox().overlaps(entity1.getHitbox())) {
                            entity2.setTriggered(true);
                            return true;
                        }
                }
            }
        }
        return false;
    }

    public Player getPlayer() {
        return (Player) entities.get("player");
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public void dispose() {
        if (!entities.isEmpty()) {
            for (Entity entity : entities.values()) {
               entity.dispose();
            }
        }
    }
}
