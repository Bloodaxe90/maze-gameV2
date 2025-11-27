package io.github.game.systems; // or io.github.game.managers

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.game.Game;
import io.github.game.entity.entities.Player;

public class CameraSystem {

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private float drift;

    public CameraSystem(float width, float height, float drift) {

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(width, height, camera);
        this.drift = drift;

        camera.position.set(width / 2, height / 2, 0);
        camera.update();
    }

    public void update(Player player) {
        float newX = camera.position.x + ((player.getPos().x + player.getSize().x / 2) - camera.position.x) * drift;
        float newY = camera.position.y + ((player.getPos().y + player.getSize().y / 2) - camera.position.y) * drift;

        if (Game.WORLD_SIZE.x > camera.viewportWidth) {
            newX = MathUtils.clamp(newX, camera.viewportWidth / 2f, Game.WORLD_SIZE.x - camera.viewportWidth / 2f);
        } else {
            newX = Game.WORLD_SIZE.x / 2f;
        }

        if (Game.WORLD_SIZE.y > camera.viewportHeight) {
            newY = MathUtils.clamp(newY, camera.viewportHeight / 2f, Game.WORLD_SIZE.y - camera.viewportHeight / 2f);
        } else {
            newY = Game.WORLD_SIZE.y / 2f;
        }

        camera.position.set(newX, newY, 0);
        camera.update();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
