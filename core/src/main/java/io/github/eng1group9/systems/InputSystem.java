package io.github.eng1group9.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.eng1group9.Main;
import io.github.eng1group9.entities.Player;

/**
* Handles user input, both for movement and misc inputs.
*/
public class InputSystem {
    /**
     * Processes user input, both for movement and misc inputs.
     * @param player - the player to move based on input.
     */
    public void handle(Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            Main.instance.toggleFullscreen();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit(); // Close the game
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            Main.togglePause();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            Main.instance.showCollision = !Main.instance.showCollision;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            TriggerSystem.checkInteractTriggers(player);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Main.startGame();
        }
        

        if (!player.isFrozen()) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.move('U');
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.move('L');
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.move('D');
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.move('R');
            }
        }
    }
}
