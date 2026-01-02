package io.github.game.entity;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.game.Game;
import io.github.game.utils.io.TriggerLoader;
import io.github.game.utils.triggers.Trigger;
import io.github.game.utils.io.AnimationLoader;
import io.github.game.utils.io.MapLoader;

/**
 * The base class for all objects in the game world, like the player and enemies
 */
public abstract class Entity {

    protected String id;
    private final RectangleMapObject startingProperties; // The object from the Tiled map
    protected Vector2 position;
    protected Vector2 size;

    protected Rectangle hitbox;
    protected boolean collidable;

    // A map to hold all the animations for this entity
    protected Map<String, Animation<TextureRegion>> spriteMap = new HashMap<>();
    protected float stateTime = 0f; // Tracks time for animations
    protected TextureRegion sprite; // The current frame to be drawn

    protected Trigger trigger;
    protected boolean triggered;

    protected boolean active = true;
    protected TextureAtlas spriteAtlas;

    /**
     * The main constructor for any entity
     * @param properties The rectangle object from the Tiled map
     * @param spriteAtlas The texture atlas that contains this entities sprites
     */
    public Entity(RectangleMapObject properties,
                  TextureAtlas spriteAtlas
    ) {

        this.startingProperties = properties;
        this.spriteAtlas = spriteAtlas;

        try {
            assert startingProperties != null;
            Rectangle startArea = startingProperties.getRectangle();
            // Create a hitbox that is half the width and half the height of the sprite
            this.hitbox = new Rectangle(
                startArea.x + (startArea.width / 4f),
                startArea.y,
                startArea.width / 2f,
                startArea.height / 2f
            );
            this.position = new Vector2(startArea.x, startArea.y);
            this.size = new Vector2(startArea.width, startArea.height);
        } catch (NullPointerException e) {
            // Log an error if the object from Tiled was not found
            Gdx.app.log("ERROR", String.valueOf(e));
        }
        this.id = startingProperties.getName();

        // Set up triggers and sprites based on custom properties from Tiled
        createTrigger();
        createSprites();
        this.collidable = getStartingProperty("collidable", Boolean.class);
    }

    /**
     * Reads the 'trigger' custom property from Tiled and creates a trigger object
     */
    private void createTrigger() {
        String triggerInfo = getStartingProperty("trigger", String.class);
        if (triggerInfo != null) {
            // This messy bit adds the entities own ID into the trigger data string
            if (triggerInfo.contains(",")) {
                triggerInfo = triggerInfo.replaceFirst(",", "," + id + ",");
            } else {
                triggerInfo = triggerInfo + "," + id;
            }
        }
        // Use the TriggerLoader to create the correct type of trigger
        this.trigger = TriggerLoader.loadTrigger(triggerInfo);

    }

    /**
     * Reads the 'sprites' custom property and loads all the animations
     */
    private void createSprites() {
        String sprites = getStartingProperty("sprites", String.class);
        if (sprites != null && !sprites.isEmpty()) {
            boolean initialSpriteSet = false;
            // The property is a comma separated list, i.e. "idle/0.2,walk/0.1"
            for (String spriteInfo : sprites.split(",")) {
                int lastUnderscoreIndex = spriteInfo.lastIndexOf("/");
                if (lastUnderscoreIndex != -1) {
                    // The name is everything before the last slash
                    String name = spriteInfo.substring(0, lastUnderscoreIndex);
                    // The duration is everything after
                    float duration = Float.parseFloat(spriteInfo.substring(lastUnderscoreIndex + 1));

                    addSprite(name, duration);

                    // Set the very first animation in the list as the starting sprite
                    if (!initialSpriteSet) {
                        initialSpriteSet = true;
                        setSprite(name);
                    }
                } else {
                    Gdx.app.log("ERROR", "No duration provided for entity " + id + "s sprites");
                }
            }
        }
    }

    /**
     * Draws the entities current sprite to the screen
     * @param batch The SpriteBatch for rendering
     */
    public void render(SpriteBatch batch) {
        // Only draw if the entity is active and has a sprite
        if (active && this.sprite != null) {
            batch.draw(sprite, position.x, position.y, size.x, size.y);
        }
    };

    /**
     * Updates the entities state, like animation time
     * @param delta_t Time since the last frame
     * @param game A reference to the main game class
     */
    public void update(float delta_t, Game game) {
        if (active) {
            stateTime += delta_t;
            tryTrigger(game);
        }
    };

    /**
     * Checks if this entity has been triggered and runs the trigger logic
     * @param game A reference to the main game class
     */
    private void tryTrigger(Game game) {
        if (triggered && trigger != null) {
            trigger.trigger(game);
            triggered = false; // Reset the trigger so it only runs once
        }
    }

    /**
     * Loads an animation and adds it to the entities sprite map
     * @param name The base name of the animation frames
     * @param duration The speed of the animation
     */
    public void addSprite(String name, float duration) {
        String key = id + "_" + name;
        Animation<TextureRegion> animation = AnimationLoader.getAnimation(
            key,
            duration,
            spriteAtlas,
            Animation.PlayMode.LOOP);

        // If an animation for this specific ID isn't found (i.e. "npc1_walk"),
        // try a generic one (i.e. "npc_walk")
        if (animation == null) {
            key = id.replaceAll("\\d", "") + "_" + name;
            animation = AnimationLoader.getAnimation(
                key,
                duration,
                spriteAtlas,
                Animation.PlayMode.LOOP);
        }
        spriteMap.put(key, animation);
    }


    /**
     * Sets the entities current sprite to a frame from a specific animation
     * @param name The name of the animation to use
     */
    public void setSprite(String name) {
        if (name == null || name.isEmpty() || spriteMap.isEmpty()) return;

        // Try to get the specific animation first (i.e. "npc1_walk")
        Animation<TextureRegion> animation = spriteMap.get(id + "_" + name);

        // If that fails, try the generic version (i.e. "npc_walk")
        if (animation == null) {
            animation = spriteMap.get(id.replaceAll("\\d", "") + "_" + name);
        }

        // Get the correct frame for the current stateTime
        this.sprite = animation.getKeyFrame(stateTime);

    }


    /** @return The entities current position */
    public Vector2 getPos() {
        return new Vector2(position);
    }


    /**
     * Sets the entities X position
     * @param XPos The new X coordinate
     */
    public void setXPos(Float XPos) {
        position.x = XPos;
        hitbox.setX(XPos + (size.y / 4f));
    }


    /**
     * Sets the entities Y position
     * @param YPos The new Y coordinate
     */
    public void setYPos(Float YPos) {
        position.y = YPos;
        hitbox.setY(YPos);
    }


    /** @return The entities size */
    public Vector2 getSize() {
        return size;
    }


    /** @return The entities unique ID */
    public String getId() {
        return id;
    }


    /** @return True if the entity is currently active */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets whether the entity should be active (visible and updating)
     * @param active The new active state
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /** @return The entities hitbox for collision detection */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /**
     * A helper to get a custom property from the Tiled map object
     * @param propertyName The name of the custom property
     * @param type The data type of the property
     * @return The value of the property
     */
    protected <T> T getStartingProperty(String propertyName, Class<T> type) {
        return MapLoader.getCustomProperty(this.startingProperties, propertyName, type);
    }

    /** @return True if the entity can collide with other things */
    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    /**
     * Sets the triggered state of the entities trigger
     * @param triggered The new triggered state
     */
    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    /**
     * Cleans up the entities assets to prevent memory leaks
     */
    public void dispose() {
        spriteAtlas.dispose();
    }

//    public String getAnimationKeyFromSprite() {
//        for (Map.Entry<String, Animation<TextureRegion>> entry : spriteMap.entrySet()) {
//            String key = entry.getKey();
//            Animation<TextureRegion> animation = entry.getValue();
//
//            TextureRegion[] frames = animation.getKeyFrames();
//
//            for (TextureRegion frame : frames) {
//                if (frame == this.sprite) {
//                    return key;
//                }
//            }
//        }
//        return null;
//    }
}
