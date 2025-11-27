package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.game.systems.*;
import io.github.game.entity.entities.Player;


public class Game extends ApplicationAdapter {

    private InputSystem inputSystem;
    private UpdateSystem updateSystem;
    private RenderSystem renderSystem;
    private CameraSystem cameraSystem;
    private EnvironmentSystem environmentSystem;
    private EntitySystem entitySystem;
    private UiSystem uiSystem;

    public static boolean PLAYING = false;
    public static TiledMap MAP;
    public static final Vector2 WORLD_SIZE = new Vector2(320 * 3, 240 * 3);

    @Override
    public void create() {
        MAP = new TmxMapLoader().load("assets/maps/map.tmx");

        inputSystem = new InputSystem(this);
        updateSystem = new UpdateSystem(this);
        renderSystem = new RenderSystem(this);

        environmentSystem = new EnvironmentSystem();
        entitySystem = new EntitySystem();

        uiSystem = new UiSystem("UI");

        cameraSystem = new CameraSystem(
            Game.WORLD_SIZE.x / 2f, Game.WORLD_SIZE.y / 2f, 0.5f
        );
    }

    @Override
    public void render() {
        if (isGameOver()) return;
        inputSystem.handleInputs();
        updateSystem.update();
        renderSystem.render();
    }

    private boolean isGameOver() {
        if (uiSystem.getPauseMenu().getText().toLowerCase().replaceAll("[^a-z]", "").contains("gameover")) {
            PLAYING = false;
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        cameraSystem.resize(width, height);
        uiSystem.resize(width, height);
    }

    @Override
    public void dispose() {
        MAP.dispose();
        renderSystem.dispose();
        environmentSystem.dispose();
        entitySystem.dispose();
        uiSystem.dispose();
    }

    public CameraSystem getCameraSystem() {
        return cameraSystem;
    }

    public EnvironmentSystem getEnvironmentSystem() {
        return environmentSystem;
    }

    public EntitySystem getEntitySystem() {
        return entitySystem;
    }

    public UiSystem getUiSystem() {
        return uiSystem;
    }
}
