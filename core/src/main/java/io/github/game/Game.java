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

import io.github.game.entity.EntityManager;
import io.github.game.entity.entities.Player;
import io.github.game.ui.UiManager;


public class Game extends ApplicationAdapter {

    private SpriteBatch spriteBatch;
    private CameraManager cameraManager;

    private EnvironmentManager environmentManager;
    private EntityManager entityManager;

    public boolean playing;

    public static TiledMap MAP;
    public static final Vector2 WORLD_SIZE = new Vector2(320 * 3, 240 * 3);
    private UiManager uiManager;

    @Override
    public void create() {

        MAP = new TmxMapLoader().load("assets/maps/map.tmx");

        spriteBatch = new SpriteBatch();

        environmentManager = new EnvironmentManager();
        entityManager = new EntityManager();

        uiManager = new UiManager("UI");

        cameraManager = new CameraManager(
            Game.WORLD_SIZE.x / 2f, Game.WORLD_SIZE.y / 2f, 0.5f
        );

        playing = false;
    }

    @Override
    public void render() {
        input();
        update();
        draw();
    }

    public void input() {
        if (isGameOver()) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
        ) {
            playing = !playing;
        }

        if (playing) {
            if (uiManager.getDialogueBox().isVisible()) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    if (!uiManager.getDialogueBox().isFinished()) {
                        uiManager.getDialogueBox().skip();
                    } else {
                        uiManager.getDialogueBox().hideDialogue();
                    }
                }
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) uiManager.getToastBar().addToast("Sigma on the wall whos the fiarest of them all its me sigma rizzler", Color.BLUE);
            Player player = entityManager.getPlayer();
            player.setInteract(Gdx.input.isKeyPressed(Input.Keys.E));
            player.setMovingUp(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W));
            player.setMovingDown(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S));
            player.setMovingLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A));
            player.setMovingRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D));
        }
    }


    public void update() {
        float delta_t = Gdx.graphics.getDeltaTime();
        if (playing) {

            if (!uiManager.getDialogueBox().isVisible()) {
                entityManager.update(delta_t, this);
            }
        }
        cameraManager.update(entityManager.getPlayer());
        uiManager.update(delta_t, playing, entityManager.getPlayer());
    }


    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        uiManager.getViewport().apply();
        cameraManager.getViewport().apply();

        OrthographicCamera camera = this.cameraManager.getCamera();
        environmentManager.renderBackground(camera);
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();

        entityManager.render(spriteBatch);

        spriteBatch.end();
        environmentManager.renderForeground(camera);

        uiManager.render();
    }


    private boolean isGameOver() {
        if (uiManager.getPauseMenu().getText().toLowerCase().replaceAll("[^a-z]", "").contains("gameover")) {
            playing = false;
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
        uiManager.resize(width, height);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public EnvironmentManager getEnvironmentManager() {
        return environmentManager;
    }

    public UiManager getUiManager() {
        return uiManager;
    }

    @Override
    public void dispose() {
        MAP.dispose();
        spriteBatch.dispose();
        environmentManager.dispose();
        entityManager.dispose();
        uiManager.dispose();
    }
}
