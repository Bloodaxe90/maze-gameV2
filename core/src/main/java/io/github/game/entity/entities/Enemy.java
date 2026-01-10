package io.github.game.entity.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;

import io.github.game.Game;
import io.github.game.entity.MovableEntity;

/**
 * CHANGES:
 * This class is the renamed Dean class from the original code
 * it works much in the same way, however it is less hardcoded and allows for
 * more general Enemy instance with some additional functionality not seen in
 * the original game
 *
 * The class for enemy entities
 * It extends MovableEntity to get movement capabilities and can either
 * follow the player or walk along a predefined path
 */
public class Enemy extends MovableEntity {

    private final float range; // The detection range for the enemy, in tiles
    private float trackSpeed;

    private String[] path = null;
    private int currentPathIndex;

    private Vector2 pathTargetPosition; // The next coordinate the enemy is walking towards

    /**
     * Constructor for an Enemy
     * @param properties The rectangle object from the Tiled map
     * @param spriteAtlas The texture atlas for the enemy sprites
     */
    public Enemy(RectangleMapObject properties,
                 TextureAtlas spriteAtlas) {

        super(properties, spriteAtlas);

        this.range = getStartingProperty("range", Float.class);

        // Path is defined in Tiled as a string like "U2,L1,D2,R1"
        String pathInfo = getStartingProperty("path", String.class);
        if (pathInfo != null && !pathInfo.isEmpty()) {
            this.path = pathInfo.split(",");
            this.currentPathIndex = 0; // Start at the beginning of the path
            this.pathTargetPosition = new Vector2(position); // Start with no target
        }

        // trackSpeed is a separate speed used only when chasing the player
        try {
            this.trackSpeed = getStartingProperty("trackSpeed", Float.class);
        } catch (Exception e) {
            this.trackSpeed = speed; // Default to normal speed if not specified
        }
    }

    @Override
    public void update(float delta_t, Game game) {
        super.update(delta_t, game);

        if (!alive) return;

        Vector2 velocity = new Vector2(0f, 0f);

        Vector2 dirToPlayer = game.getEntitySystem().getPlayer().getPos().sub(this.position);
        float distanceToPlayer = dirToPlayer.len(); // Use len() for cleaner distance calc

        // If player is in range, TRACK the player
        if (distanceToPlayer <= range * Game.TILE_SIZE || range < 0) {
            velocity = dirToPlayer.nor().scl(this.trackSpeed); // Normalize and scale by speed
        }
        // Otherwise, if a path is defined, FOLLOW the path
        else if (path != null) {
            // Check if we have reached our current target position
            if (position.dst(pathTargetPosition) < 2.0f) { // Using a small tolerance
                setNextPathTarget();
            }

            // Calculate velocity to move towards the current target
            Vector2 dirToTarget = pathTargetPosition.cpy().sub(position);
            velocity = dirToTarget.nor().scl(this.speed);
        }

        // Calculate the potential new position
        float newX = position.x + (velocity.x * delta_t);
        float newY = position.y + (velocity.y * delta_t) ;

        if (!isCollidable()) {
            setXPos(newX);
            setYPos(newY);
            updateSprite(velocity); // Still need to update sprite
            return;
        }

        // Store old position for collision response
        float oldX = position.x;
        float oldY = position.y;

        // Try moving horizontally
        setXPos(newX);
        if (game.getEnvironmentSystem().checkCollision(this)) {
            setXPos(oldX); // Move back if a collision occurred
        }

        // Try moving vertically
        setYPos(newY);
        if (game.getEnvironmentSystem().checkCollision(this)) {
            setYPos(oldY); // Move back if a collision occurred
        }

        // Update the enemy's sprite to match its movement direction
        updateSprite(velocity);
    }

    /**
     * Parses the current path instruction and sets the next target coordinate
     */
    private void setNextPathTarget() {
        if (path == null || path.length == 0) return;

        String moveInfo = path[currentPathIndex];

        try {
            String direction = moveInfo.substring(0, 1).toLowerCase();
            float numTiles = Float.parseFloat(moveInfo.substring(1));
            float distance = numTiles * Game.TILE_SIZE;

            // Update the target position based on the command
            switch (direction) {
                case "u":
                    pathTargetPosition.y += distance;
                    break;
                case "d":
                    pathTargetPosition.y -= distance;
                    break;
                case "l":
                    pathTargetPosition.x -= distance;
                    break;
                case "r":
                    pathTargetPosition.x += distance;
                    break;
            }

            // Move to the next instruction in the path, looping back to the start
            currentPathIndex++;
            if (currentPathIndex >= path.length) {
                currentPathIndex = 0;
            }

        } catch (Exception e) {
            Gdx.app.log("ERROR", "Invalid path format for " + id + " enemy: " + moveInfo);
            // If path is broken, stop pathfinding
            path = null;
        }
    }

    public void setTrackSpeed(float trackSpeed) {
        this.trackSpeed = trackSpeed;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public void setCurrentPathIndex(int currentPathIndex) {
        this.currentPathIndex = currentPathIndex;
    }
}
