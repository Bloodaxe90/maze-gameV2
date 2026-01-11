package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import io.github.game.Game;
import io.github.game.utils.io.AudioPlayer;

/**
 * NEW CLASS: (explanation for why in RenderSystem)
 *
 * Updates the state of all game objects each frame
 */
public class UpdateSystem {

    Game game;

    /**
     * Constructor for the update system
     * @param game A reference to the main game class to access other systems
     */
    public UpdateSystem(Game game) {
        this.game = game;
    }

    /**
     * Called every frame to update game logic like movement and state changes
     */
    public void update() {
        // Get the time that has passed since the last frame
        float delta_t = Gdx.graphics.getDeltaTime();

        // Only update the main game logic if the game is not paused
        if (Game.PLAYING) {

            // Don't update entities if a dialogue box is open
            // This effectively freezes the player and enemies during conversations
            if (!game.getUiSystem().getDialogueBox().isVisible()) {
                game.getEntitySystem().update(delta_t, game);
            }
        }

        AudioPlayer.setMusicEnabled(Game.PLAYING);

        // The camera and UI should update regardless of the pause state
        // This allows the camera to keep following the player even when paused,
        // and lets the UI update its animations or timers
        game.getCameraSystem().update(game.getEntitySystem().getPlayer());
        game.getUiSystem().update(delta_t, game.getEntitySystem().getPlayer());
    }
}
