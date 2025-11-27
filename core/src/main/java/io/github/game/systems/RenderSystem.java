package io.github.game.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.game.Game;

public class RenderSystem {

    SpriteBatch spriteBatch;
    Game game;

    public RenderSystem(Game game) {
        this.game = game;
        spriteBatch = new SpriteBatch();
    }

    public void render() {
        ScreenUtils.clear(Color.BLACK);

        game.getUiSystem().getViewport().apply();
        game.getCameraSystem().getViewport().apply();

        OrthographicCamera camera = this.game.getCameraSystem().getCamera();
        game.getEnvironmentSystem().renderBackground(camera);
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        game.getEntitySystem().render(spriteBatch);

        spriteBatch.end();
        game.getEnvironmentSystem().renderForeground(camera);

        game.getUiSystem().render();
    }

    public void dispose() {
        spriteBatch.dispose();
    }
}
