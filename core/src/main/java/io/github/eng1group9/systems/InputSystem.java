package io.github.eng1group9.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.eng1group9.Main;
import io.github.eng1group9.entities.Player;

public class InputSystem {
    /**
     * Move the player if directional keys are being pressed.
     */
    public void handle(Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            Main.instance.toggleFullscreen();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit(); // Close the game
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            Main.instance.togglePause();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            Main.instance.showCollision = !Main.instance.showCollision;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Main.instance.tryInteract();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Main.instance.startGame();
        }
        

        if (!player.isFrozen()) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.move('U');
                player.changeAnimation(1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.move('L');
                player.changeAnimation(3);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.move('D');
                player.changeAnimation(0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.move('R');
                player.changeAnimation(2);
            }
        }
    }
}
