package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;

import io.github.game.systems.CameraSystem;
import io.github.game.systems.EntitySystem;
import io.github.game.systems.EnvironmentSystem;
import io.github.game.systems.InputSystem;
import io.github.game.systems.RenderSystem;
import io.github.game.systems.UiSystem;
import io.github.game.systems.UpdateSystem;
import io.github.game.utils.io.AudioPlayer;

/**
 * CHANGES:
 * This was the Main class in the forked project, and it has changed a lot since then that class
 * was acting as a GOD CLASS attempting to handle rendering, logic and entity behaviour
 * hard coded within itself. Many changes have been made to the class; the hardcoded
 * parts were removed and re implemented in other classes (Most in EntitySystem and its managed Entity classes)
 * as more general version of the original and the rendering, logic and input handling
 * was passed onto other System classes leaving the Game class with the sole responsibility
 * of organising the order which the systems should be run and initialising the systems
 *
 * The main class for the game
 * sets up all the systems and runs the main game loop
 */
public class Game extends ApplicationAdapter {

    // These systems manage the different aspects of our game
    private InputSystem inputSystem;
    private UpdateSystem updateSystem;
    private RenderSystem renderSystem;
    private CameraSystem cameraSystem;
    private EnvironmentSystem environmentSystem;
    private EntitySystem entitySystem;
    private UiSystem uiSystem;

    // Static variables that can be accessed from anywhere
    public static boolean PLAYING = false;
    public static TiledMap MAP;
    public static int TILE_SIZE;
    public static Vector2 WORLD_SIZE;
    public static final Vector2 SCREEN_SIZE = new Vector2(1920, 1440);

    @Override
    public void create() {
        // Load the map from the Tiled editor
        MAP = new TmxMapLoader().load("assets/maps/map.tmx");
        TILE_SIZE = MAP.getProperties().get("tilewidth", Integer.class);

        // Calculate the world size in pixels from the map properties
        WORLD_SIZE = new Vector2(
            MAP.getProperties().get("width", Integer.class) * TILE_SIZE,
            MAP.getProperties().get("height", Integer.class) * TILE_SIZE
        );

        // Create instances of all systems
        inputSystem = new InputSystem(this);
        updateSystem = new UpdateSystem(this);
        renderSystem = new RenderSystem(this);

        environmentSystem = new EnvironmentSystem();
        entitySystem = new EntitySystem();

        uiSystem = new UiSystem("UI");

        // Set up the camera with a specific viewport and zoom
        cameraSystem = new CameraSystem(
            240, 180, 1.5f
        );
        AudioPlayer.playTrack("soundtrack", 0.2f);
    }

    @Override
    public void render() {
        // The main game loop: handle input, update logic, then draw
        inputSystem.handleInputs();
        updateSystem.update();
        renderSystem.render();
    }

    /**
     * Checks if the pause menu text contains "gameover" to end the game.
     * @return true if the game is over otherwise false
     */
    public boolean isGameOver() {
        // This is a simple way to check game state by reading from the UI
        String status = uiSystem.getPauseMenu().getText().toLowerCase().replaceAll("[^a-z]", "");
        if (status.contains("gameover") && !getUiSystem().getDialogueBox().isVisible()) {
            if (PLAYING) {
                if (status.contains("win")) {
                    AudioPlayer.playSound("win", 3f);
                } else {
                    AudioPlayer.playSound("lose", 3f);
                }
            }
            PLAYING = false;
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        // Make sure our cameras adjust when the window is resized
        cameraSystem.resize(width, height);
        uiSystem.resize(width, height);
    }

    @Override
    public void dispose() {
        // Clean up memory when the game closes
        MAP.dispose();
        renderSystem.dispose();
        environmentSystem.dispose();
        entitySystem.dispose();
        uiSystem.dispose();
    }

    /** @return The camera system instance */
    public CameraSystem getCameraSystem() {
        return cameraSystem;
    }

    /** @return The environment system instance */
    public EnvironmentSystem getEnvironmentSystem() {
        return environmentSystem;
    }

    /** @return The entity system instance */
    public EntitySystem getEntitySystem() {
        return entitySystem;
    }

    /** @return The UI system instance */
    public UiSystem getUiSystem() {
        return uiSystem;
    }
}
