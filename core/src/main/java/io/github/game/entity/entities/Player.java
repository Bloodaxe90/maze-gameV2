package io.github.game.entity.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.game.Game;
import io.github.game.entity.Entity;
import io.github.game.entity.MovableEntity;
import io.github.game.systems.EntitySystem;
import io.github.game.ui.elements.Hotbar;
import io.github.game.ui.elements.Item;
import io.github.game.utils.io.AudioPlayer;

/**
 * The main player class, controlled by the user
 * It handles movement and inventory
 */
public class Player extends MovableEntity {

    // These booleans track which movement keys are currently pressed
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private boolean interact = false;

    private final Array<Item> inventory;
    private float footstepTimer = 0;
    private final float footstepTimeout;

    // We store the original speed so we can apply temporary boosts/debuffs
    private final float baseSpeed;

    private final String boostItem;
    private final float boostModifier;

    private float speedModifier = 1.0f;
    private float speedModifierDuration = 0f;

    /**
     * Constructor for the Player
     * @param properties The rectangle object from the Tiled map
     * @param spriteAtlas The texture atlas containing the player's sprites
     */
    public Player(RectangleMapObject properties, TextureAtlas spriteAtlas) {
        super(properties, spriteAtlas);
        this.inventory = new Array<>(Hotbar.NUM_SLOTS);
        this.footstepTimeout = 1 / getStartingProperty("footstepFrequency", Float.class);

        // Store the original speed from the map as our base speed
        this.baseSpeed = this.speed;

        String boostItemInfo = getStartingProperty("boostItem", String.class);
        int lastUnderscoreIndex = boostItemInfo.lastIndexOf("/");
        if (lastUnderscoreIndex != -1) {
            // The name is everything before the last slash
            this.boostItem = boostItemInfo.substring(0, lastUnderscoreIndex);
            // The duration is everything after
            this.boostModifier = Float.parseFloat(boostItemInfo.substring(lastUnderscoreIndex + 1));
        } else {
            this.boostItem = null;
            this.boostModifier = -1;
        }

        // Load any starting items from the Tiled map properties
        String itemInfo = getStartingProperty("items", String.class);
        if (!itemInfo.isEmpty()) {
            for (String item : itemInfo.split(",")) {
                addItem(item);
            }
        }
    }

    @Override
    public void update(float delta_t, Game game) {
        super.update(delta_t, game);

        if (interact) {
            game.getEntitySystem().checkInteraction();
        }

        // Handle any temporary speed effects
        if (speedModifierDuration > 0) {
            speedModifierDuration -= delta_t;
            if (speedModifierDuration <= 0) {
                speedModifier = 1.0f; // Reset to normal when the timer runs out
            }
        }

        // Update the current speed based on items and modifiers
        if (hasItem(boostItem)) {
            this.speed = baseSpeed * boostModifier * speedModifier;
        } else {
            this.speed = baseSpeed * speedModifier;
        }

        // Calculate velocity based on which keys are pressed
        Vector2 velocity = new Vector2(0f, 0f);
        if (movingLeft) velocity.x = -speed;
        if (movingRight) velocity.x = speed;
        if (movingUp) velocity.y = speed;
        if (movingDown) velocity.y = -speed;

        // Calculate potential new position
        float newX = position.x + (velocity.x * delta_t);
        float newY = position.y + (velocity.y * delta_t) ;

        if (!isCollidable()) {
            setXPos(newX);
            setYPos(newY);
            return;
        }

        // Store the old position in case of a collision
        float oldX = position.x;
        float oldY = position.y;

        // Try moving on the X-axis
        setXPos(newX);
        // Check for collision with the edge of the world
        if (position.x < 0 || position.x + size.x > Game.WORLD_SIZE.x) {
            velocity.x = 0;
            setXPos(MathUtils.clamp(position.x, 0, Game.WORLD_SIZE.x - size.x));
        }
        // Check for collision with map objects or other entities
        else if (game.getEnvironmentSystem().checkCollision(this) || game.getEntitySystem().checkCollision(this) != null) {
            velocity.x = 0;
            setXPos(oldX); // Move back if a collision occurred
        }

        // Try moving on the Y-axis
        setYPos(newY);
        if (position.y < 0 || position.y + size.y > Game.WORLD_SIZE.y) {
            velocity.y = 0;
            setYPos(MathUtils.clamp(position.y, 0, Game.WORLD_SIZE.y - size.y));
        } else if (game.getEnvironmentSystem().checkCollision(this) || game.getEntitySystem().checkCollision(this) != null) {
            velocity.y = 0;
            setYPos(oldY);
        }

        // Update sprite and sound effects based on final movement
        updateSprite(velocity);
        updateSFX(delta_t, velocity);
    }


    /**
     * Temporarily changes the player's speed by a multiplier
     * @param multiplier The speed multiplier (e.g. 2.0 for double speed)
     * @param duration The duration of the effect in seconds
     */
    public void applySpeedModifier(float multiplier, float duration) {
        this.speedModifier = multiplier;
        this.speedModifierDuration = duration;
    }


    /**
     * Handles playing footstep sound effects
     * @param delta_t Time since last frame
     * @param velocity The player's current velocity
     */
    private void updateSFX(float delta_t, Vector2 velocity) {
        // If not moving, reset the timer
        if (velocity.isZero()) {
            footstepTimer = 0;
        } else {
            // If the timer has exceeded the timeout, play a sound
            if (footstepTimer > footstepTimeout || footstepTimer == 0) {
                // Play a random footstep sound with a random pitch
                AudioPlayer.playSound("footstep" + MathUtils.random(1, 3), 0.5f, MathUtils.random(0.5f, 3f));
                footstepTimer = 0; // Reset timer
            }
            footstepTimer += delta_t;
        }
    }

    /**
     * Adds an item to the player's inventory
     * @param itemName The name of the item to add
     */
    public void addItem(String itemName) {
        if (inventory.size < Hotbar.NUM_SLOTS) {
            inventory.add(new Item(itemName));
        }
    }

    /**
     * Checks if the player has a specific item
     * @param itemName The name of the item to check for
     * @return True if the item is in the inventory
     */
    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) return true;
        }
        return false;
    }

    /**
     * Removes an item from the player's inventory
     * @param itemName The name of the item to remove
     */
    public void removeItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) inventory.removeValue(item, false);
        }
    }

    // Setters for the movement booleans, called by the InputSystem

    public void setMovingUp(boolean movingUp) { this.movingUp = movingUp; }
    public void setMovingDown(boolean movingDown) { this.movingDown = movingDown; }
    public void setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }
    public void setInteract(boolean interact) {this.interact = interact; }

    /** @return The player's inventory */
    public Array<Item> getInventory() { return inventory; }

    /** @return True if the interact key is being pressed */
    public boolean isInteract() {
        return interact;
    }


    /**
     * A helper method to immediately stop all player movement
     */
    public void stopMoving() {
        movingLeft = movingRight = movingUp = movingDown = false;
    }
}
