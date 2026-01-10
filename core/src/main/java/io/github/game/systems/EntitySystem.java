package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

import com.badlogic.gdx.math.Rectangle;
import io.github.game.Game;
import io.github.game.entity.Entity;
import io.github.game.entity.entities.Player;
import io.github.game.utils.io.MapLoader;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * NEW CLASS: (explanation for why in Game)
 * This class also takes on check for collision between entity objects found
 * in the CollisionSystem class in the forked code
 *
 * Manages all the entities in the game
 * This includes creating, updating, rendering, and disposing of them
 */
public class EntitySystem {

    // A map to hold all our entities, using their unique ID from Tiled as the key
    protected Map<String, Entity> entities = new HashMap<>();

    /**
     * Constructor for the EntitySystem
     * It automatically finds and creates all entities defined in the Tiled map
     */
    public EntitySystem() {
        // Use the Reflections library to find all classes that are a type of Entity
        Set<Class<? extends Entity>> childClasses = new Reflections("io.github.game.entity").getSubTypesOf(Entity.class);

        // Loop through each entity class we found
        for (Class<? extends Entity> childClass : childClasses) {
            // By convention the layer name in Tiled should match the class name
            String layerName = childClass.getSimpleName();

            // Get all the rectangle objects from that layer
            for (RectangleMapObject properties : MapLoader.getLayerRectangles(layerName)) {
                try {
                    // This is the reflection part it creates a new instance of the entity
                    // by finding its constructor and calling it with the map data
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

    /**
     * Renders all active entities
     * @param batch The SpriteBatch for drawing
     */
    public void render(SpriteBatch batch) {
        if (!entities.isEmpty()) {
            // Render all entities that are NOT the player
            for (Entity entity : entities.values()) {
                if (!(entity instanceof Player)) {
                    entity.render(batch);
                }
            }

            // Render the player last
            Player player = getPlayer();
            if (player != null) {
                player.render(batch);
            }
        }
    }


    /**
     * Updates all entities and removes any that have become inactive
     * @param delta_t Time since the last frame
     * @param game A reference to the main game class
     */
    public void update(float delta_t, Game game) {
        // We use an iterator so we can safely remove entities from the map while looping
        Iterator<Entity> iterator = entities.values().iterator();

        while (iterator.hasNext()) {
            Entity entity = iterator.next();

            // Update all entities that are NOT the player
            if (!(entity instanceof Player)) {
                entity.update(delta_t, game);
            }

            // If an entity has been set to inactive, remove it from the game
            if (!entity.isAlive() && !game.getUiSystem().getDialogueBox().isVisible()) {
                iterator.remove();
            }
        }
        // Update the player last
        Player player = getPlayer();
        if (player != null) {
            player.update(delta_t, game);
        }
    }

    /**
     * Checks if a given entity is colliding with any other collidable entity
     * @param entity1 The entity to check
     * @return Collided Entity if a collision occurred, otherwise null
     */
    public Entity checkCollision(Entity entity1) {
        if (entity1.isCollidable()) {
            if (!entities.isEmpty()) {
                // Loop through every other entity to check for collision
                for (Entity entity2 : entities.values()) {
                    // Make sure the entities are collidable and not checking against themselves
                    if (entity2.isCollidable() && !entity1.equals(entity2)) {
                        if (entity2.getHitbox().overlaps(entity1.getHitbox())) {
                            entity2.setTriggered(true);
                            return entity2;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a given entity is within interaction distance from the player
     * @return Entity to interact with, otherwise null
     */
    public Entity checkInteraction() {
        Player player = getPlayer();
        if (player != null) {
            if (player.isCollidable()) {
                if (!entities.isEmpty()) {
                    Rectangle dilated_hitbox = new Rectangle(player.getHitbox());
                    int dilation = 4;
                    dilated_hitbox.x -= dilation;
                    dilated_hitbox.y -= dilation;
                    dilated_hitbox.width += dilation * 2;
                    dilated_hitbox.height += dilation * 2;

                    for (Entity entity2 : entities.values()) {
                        // Make sure the entities are collidable and not checking against themselves
                        if (entity2.isCollidable() && !player.equals(entity2)) {
                            if (entity2.getHitbox().overlaps(dilated_hitbox)) {
                                entity2.setTriggered(true);
                                return entity2;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

        /** @return The Player entity instance */
    public Player getPlayer() {
        // We assume the player object in Tiled is always named "player"
        return (Player) entities.get("player");
    }

    /** @return The map of all entities */
    public Map<String, Entity> getEntities() {
        return entities;
    }

    /**
     * Cleans up all the entities to prevent memory leaks
     */
    public void dispose() {
        if (!entities.isEmpty()) {
            for (Entity entity : entities.values()) {
                entity.dispose();
            }
        }
    }
}
