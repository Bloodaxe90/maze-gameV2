package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.game.Game;
import io.github.game.entity.entities.Player;

import static io.github.game.Game.PLAYING;

/**
 * Handles all player input from the keyboard and mouse
 */
public class InputSystem {

    Game game;

    /**
     * Constructor for the input system
     * @param game A reference to the main game class to access other systems
     */
    public InputSystem(Game game) {
        this.game = game;
    }

    /**
     * Checks for keyboard and mouse events each frame
     */
    public void handleInputs() {
        if (game.isGameOver()) return;

        // P or ESC or Left click toggles the pause/play state of the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
        ) {
            PLAYING = !PLAYING;
        }

        // Only handle gameplay inputs if the game is not paused
        if (PLAYING) {
            // If a dialogue box is visible only allow input to control the dialogue
            if (game.getUiSystem().getDialogueBox().isVisible()) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    if (!game.getUiSystem().getDialogueBox().isFinished()) {
                        // If the text is still typing skip to the end
                        game.getUiSystem().getDialogueBox().skip();
                    } else {
                        // If the text is finished, close the dialogue box
                        game.getUiSystem().getDialogueBox().hideDialogue();
                    }
                }
                // Don't process any other game inputs while in dialogue
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) game.getUiSystem().getToastBar().addToast("Sigma on the wall whos the fiarest of them all its me sigma rizzler", Color.BLUE);

            // Get the player to update its movement states
            Player player = game.getEntitySystem().getPlayer();
            player.setInteract(Gdx.input.isKeyPressed(Input.Keys.E));

            // Handle WASD and Arrow keys for movement
            player.setMovingUp(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W));
            player.setMovingDown(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S));
            player.setMovingLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A));
            player.setMovingRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D));
        }
    }
}
