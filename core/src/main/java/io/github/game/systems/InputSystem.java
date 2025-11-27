package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import io.github.game.Game;
import io.github.game.entity.entities.Player;

import static io.github.game.Game.PLAYING;

public class InputSystem {

    Game game;

    public InputSystem(Game game) {
        this.game = game;
    }

    public void handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
        ) {
            PLAYING = !PLAYING;
        }

        if (PLAYING) {
            if (game.getUiSystem().getDialogueBox().isVisible()) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    if (!game.getUiSystem().getDialogueBox().isFinished()) {
                        game.getUiSystem().getDialogueBox().skip();
                    } else {
                        game.getUiSystem().getDialogueBox().hideDialogue();
                    }
                }
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) game.getUiSystem().getToastBar().addToast("Sigma on the wall whos the fiarest of them all its me sigma rizzler", Color.BLUE);
            Player player = game.getEntitySystem().getPlayer();
            player.setInteract(Gdx.input.isKeyPressed(Input.Keys.E));
            player.setMovingUp(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W));
            player.setMovingDown(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S));
            player.setMovingLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A));
            player.setMovingRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D));
        }
    }
}
