package io.github.game.systems;

import com.badlogic.gdx.Gdx;
import io.github.game.Game;

public class UpdateSystem {

    Game game;

    public UpdateSystem(Game game) {
        this.game = game;
    }

    public void update() {
        float delta_t = Gdx.graphics.getDeltaTime();

        if (Game.PLAYING) {

            if (!game.getUiSystem().getDialogueBox().isVisible()) {
                game.getEntitySystem().update(delta_t, game);
            }
        }

        game.getCameraSystem().update(game.getEntitySystem().getPlayer());
        game.getUiSystem().update(delta_t, Game.PLAYING, game.getEntitySystem().getPlayer());
    }
}
