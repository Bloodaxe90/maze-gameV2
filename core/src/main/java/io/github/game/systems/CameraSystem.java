package io.github.game.systems; // or io.github.game.managers

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.Game;
import io.github.game.entity.entities.Player;

/**
 * Manages the game camera and its movement
 *
 * This system handles:
 * - Creating the camera and viewport
 * - Following the player smoothly
 * - Clamping the camera to the world boundaries
 */
public class CameraSystem {

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private float drift; // Controls how fast the camera follows the player

    /**
     * Constructor for the camera system
     *
     * @param width The virtual width of the camera's view
     * @param height The virtual height of the camera's view
     * @param drift The smoothness factor for camera movement (e.g. 0.1 is slow 1.0 is instant)
     */
    public CameraSystem(float width, float height, float drift) {
        this.camera = new OrthographicCamera();
        // Use a FitViewport to scale our game world without stretching it
        this.viewport = new FitViewport(width, height, camera);
        this.drift = drift;

        // Center the camera initially
        camera.position.set(width / 2, height / 2, 0);
        camera.update();
    }

    /**
     * Updates the camera's position to smoothly follow the player
     * @param player The player entity to follow
     */
    public void update(Player player) {
        // This is a linear interpolation (lerp) for smooth camera movement
        // It moves the camera a fraction of the distance towards the player each frame
        float newX = camera.position.x + ((player.getPos().x + player.getSize().x / 2) - camera.position.x) * drift;
        float newY = camera.position.y + ((player.getPos().y + player.getSize().y / 2) - camera.position.y) * drift;

        // Make sure the camera doesn't go outside the map boundaries on the X-axis
        if (Game.WORLD_SIZE.x > camera.viewportWidth) {
            newX = MathUtils.clamp(newX, camera.viewportWidth / 2f, Game.WORLD_SIZE.x - camera.viewportWidth / 2f);
        } else {
            // If the world is smaller than the screen, just center the camera
            newX = Game.WORLD_SIZE.x / 2f;
        }

        // Make sure the camera doesn't go outside the map boundaries on the Y-axis
        if (Game.WORLD_SIZE.y > camera.viewportHeight) {
            newY = MathUtils.clamp(newY, camera.viewportHeight / 2f, Game.WORLD_SIZE.y - camera.viewportHeight / 2f);
        } else {
            newY = Game.WORLD_SIZE.y / 2f;
        }

        camera.position.set(newX, newY, 0);
        camera.update();
    }

    /**
     * Updates the viewport when the game window is resized
     * @param width The new window width
     * @param height The new window height
     */
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /** @return The OrthographicCamera instance */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /** @return The Viewport instance */
    public Viewport getViewport() {
        return viewport;
    }
}
