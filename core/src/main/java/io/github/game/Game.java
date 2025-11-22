package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.game.entities.enemy.EnemyManager;
import io.github.game.entities.player.Player;
import io.github.game.ui.UiManager;
import io.github.game.utils.io.AudioPlayer;


public class Game extends ApplicationAdapter {

    private SpriteBatch spriteBatch;
    private FitViewport gameViewport;
    private OrthographicCamera gameCamera;

    private EnvironmentManager environmentManager;
    private Player player;
    private EnemyManager enemyManager;

    public boolean playing;

    public static TiledMap MAP;
    public static final Vector2 WORLD_SIZE = new Vector2(320 * 3, 240 * 3);
    private UiManager ui;

    @Override
    public void create() {

        MAP = new TmxMapLoader().load("assets/maps/map.tmx");

        spriteBatch = new SpriteBatch();

        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(WORLD_SIZE.x, WORLD_SIZE.y, gameCamera);

        environmentManager = new EnvironmentManager();
        player = new Player(
            "player",
                "Entity",
            3f, 100,
            new TextureAtlas("assets/atlas/player.atlas"));

        enemyManager = new EnemyManager(new TextureAtlas("assets/atlas/enemies.atlas"));

        ui = new UiManager(new TextureAtlas("assets/ui/ui.atlas"), gameViewport);

        playing = false;
    }

    @Override
    public void render() {
        input();
        logic();
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
            if (ui.getDialogueBox().isVisible()) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    if (!ui.getDialogueBox().isFinished()) {
                        ui.getDialogueBox().skip();
                    } else {
                        ui.getDialogueBox().hideDialogue();
                    }
                }
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) ui.getToastBar().addToast("Sigma on the wall whos the fiarest of them all its me sigma rizzler", Color.BLUE);

            player.setInteract(Gdx.input.isKeyJustPressed(Input.Keys.E));
            player.setMovingUp(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W));
            player.setMovingDown(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S));
            player.setMovingLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A));
            player.setMovingRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D));
        }
    }


    public void logic() {
        float delta_t = Gdx.graphics.getDeltaTime();
        if (playing) {

            if (!ui.getDialogueBox().isVisible()) {
                enemyManager.update(delta_t, this);
            }

            player.update(delta_t, this);
        }

        ui.update(delta_t, playing, player);
    }


    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        gameViewport.apply();
        environmentManager.renderBackground(gameCamera);

        spriteBatch.setProjectionMatrix(gameViewport.getCamera().combined);
        spriteBatch.begin();

        enemyManager.render(spriteBatch);
        player.render(spriteBatch, gameViewport);

        spriteBatch.end();
        environmentManager.renderForeground(gameCamera);

        ui.render();
    }


    private boolean isGameOver() {
                if (ui.getPauseMenu().getText().toLowerCase().replaceAll("[^a-z]", "").contains("gameover")) {
            playing = false;
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
                gameViewport.update(width, height, true);
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public EnvironmentManager getEnvironmentManager() {
        return environmentManager;
    }

    public UiManager getUi() {
        return ui;
    }

    @Override
    public void dispose() {
        MAP.dispose();
        spriteBatch.dispose();
        environmentManager.dispose();
        player.dispose();
        enemyManager.dispose();
        ui.dispose();
    }
}
