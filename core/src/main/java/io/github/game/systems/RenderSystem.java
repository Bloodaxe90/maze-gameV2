package io.github.game.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.game.Game;

/**
 * Handles all the drawing for the game
 */
public class RenderSystem {

    SpriteBatch spriteBatch;
    Game game;

    /**
     * Constructor for the render system
     * @param game A reference to the main game class to access other systems
     */
    public RenderSystem(Game game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
    }

    /**
     * Draws every part of the game in the correct order
     */
    public void render() {
        // Clear the screen to a black color before drawing anything new
        ScreenUtils.clear(Color.BLACK);

        // Apply the viewport settings for the UI and the main game camera
        // This makes sure they scale correctly with the window size
        game.getUiSystem().getViewport().apply();
        game.getCameraSystem().getViewport().apply();

        // Get the main game camera and draw the background layers of the map
        OrthographicCamera camera = this.game.getCameraSystem().getCamera();
        game.getEnvironmentSystem().renderBackground(camera);

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        // Tell the entity system to draw all the entities
        game.getEntitySystem().render(spriteBatch);

        spriteBatch.end();

        // Draw the foreground layers of the map so they appear on top of the player
        game.getEnvironmentSystem().renderForeground(camera);

        // Finally draw the UI on top of everything else
        game.getUiSystem().render();
    }

    /**
     * Cleans up
     */
    public void dispose() {
        spriteBatch.dispose();
    }
}
