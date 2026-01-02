package io.github.game.entity.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

import io.github.game.Game;
import io.github.game.entity.MovableEntity;

/**
 * The class for enemy entities
 * It extends MovableEntity to get movement capabilities
 */
public class Enemy extends MovableEntity {

    // The detection range for the enemy, in tiles
    private final float range;

    /**
     * Constructor for an Enemy
     * @param properties The rectangle object from the Tiled map
     * @param spriteAtlas The texture atlas for the enemy sprites
     */
    public Enemy(RectangleMapObject properties,
                 TextureAtlas spriteAtlas) {

        super(properties, spriteAtlas);

        // Get the enemy's detection range from a custom property in Tiled
        this.range = getStartingProperty("range", Float.class);
    }

    @Override
    public void update(float delta_t, Game game) {
        super.update(delta_t, game);

        // If the enemy isn't active, don't do any logic
        if (!active) return;

        Vector2 velocity = new Vector2(0f, 0f);

        // Calculate the direction vector pointing from the enemy to the player
        Vector2 dir = game.getEntitySystem().getPlayer().getPos().sub(this.position);

        // Calculate the distance to the player using the Pythagorean theorem
        float distanceToPlayer = (float) Math.sqrt(dir.x * dir.x + dir.y * dir.y);

        // If the player is within range, or if range is negative (meaning always active)
        if (distanceToPlayer <= range * game.getEnvironmentSystem().getTileSize() || range < 0) {
            // Normalize the direction vector and multiply by speed to get velocity
            velocity.x = (dir.x / distanceToPlayer) * this.speed;
            velocity.y = (dir.y / distanceToPlayer) * this.speed;
        } else {
            // If player is out of range, stop moving
            velocity = new Vector2(0f, 0f);
        }

        // Calculate the potential new position based on velocity and time
        float newX = position.x + (velocity.x * delta_t);
        float newY = position.y + (velocity.y * delta_t) ;

        // If the enemy isn't collidable, just move it without checking walls
        if (!isCollidable()) {
            setXPos(newX);
            setYPos(newY);
            return;
        }

        // We move on one axis, check for a collision, and reset if we hit something
        // This stops the enemy from getting stuck on corners
        float oldX = position.x;
        float oldY = position.y;

        // Try moving horizontally
        setXPos(newX);
        if (game.getEnvironmentSystem().checkCollision(this)) {
            setXPos(oldX); // Collision detected, move back
        }

        // Try moving vertically
        setYPos(newY);
        if (game.getEnvironmentSystem().checkCollision(this)) {
            setYPos(oldY); // Collision detected, move back
        }

        // Update the enemy's sprite to match its movement direction
        updateSprite(velocity);
    }
}
