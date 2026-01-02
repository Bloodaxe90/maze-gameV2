package io.github.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

/**
 * An abstract class for any entity that can move around the world
 * It extends the base Entity class and adds movement related properties
 */
public abstract class MovableEntity extends Entity{

    protected float speed;
    // Tracks the last direction the entity was moving, defaults to "front"
    protected String currentSpriteDirection = "front";


    /**
     * Constructor for a MovableEntity
     * @param properties The rectangle object from the Tiled map
     * @param spriteAtlas The texture atlas containing this entities sprites
     */
    public MovableEntity(RectangleMapObject properties, TextureAtlas spriteAtlas) {
        // Call the constructor of the parent Entity class first
        super(properties, spriteAtlas);

        // Get the movement speed from the Tiled map's custom properties
        this.speed = getStartingProperty("speed", Float.class);

        // Automatically load all the standard directional animations
        // This assumes every movable entity has these animations (front, back, left, right and so on)
        for (String name : new String[]{"front", "back", "left", "right", "idlefront", "idleback", "idleleft", "idleright"}) {
            addSprite(name, 0.1f);
        }

        // Set the initial sprite to the default direction
        setSprite(currentSpriteDirection);
    }


    /**
     * Updates the entities current sprite based on its velocity
     * @param velocity The current movement velocity of the entity
     */
    public void updateSprite(Vector2 velocity) {
        // If velocity is zero, the sprite should be "idle", otherwise it's moving
        String prefix = velocity.isZero() ? "idle" : "";

        // Determine the facing direction based on the velocity vector
        if (velocity.x > 0) {
            currentSpriteDirection = "right";
        } else if (velocity.x < 0) {
            currentSpriteDirection = "left";
        } else if (velocity.y > 0) {
            currentSpriteDirection = "back"; // Y positive is usually up/back in game worlds
        } else if (velocity.y < 0) {
            currentSpriteDirection = "front"; // Y negative is usually down/front
        }

        // Combine the prefix and direction to get the final animation name
        setSprite(prefix + currentSpriteDirection);
    }


    /** @return The movement speed of the entity */
    public float getSpeed() {
        return speed;
    }
}
