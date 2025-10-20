package io.github.eng1group9;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;

    private boolean isFullscreen = false;

    private TiledMap testMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera = new OrthographicCamera();

    private Texture playerSpriteSheet;
    private TextureRegion[][] playerFrames;
    private Sprite player;

    private FitViewport viewport;

    // Config
    private float playerSpeed = 100;


    @Override
    public void create() {
        batch = new SpriteBatch();
        setupWorld();
        setupPlayer();
    }

    public void setupWorld() {
        testMap = new TmxMapLoader().load("World/testMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(testMap);
        camera.setToOrtho(false, 480, 320);
        camera.update();
        viewport = new FitViewport(480, 320, camera);
    }


    public void setupPlayer() {
        playerSpriteSheet = new Texture("Characters/playerAnimations.png");
        playerFrames = TextureRegion.split(playerSpriteSheet, 32, 32);
        player = new Sprite(playerFrames[0][1]);
        player.translate(16, 532);
        player.setSize(64, 64);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    public void input() {
        // Process user inputs here
        playerInputs();
        miscInputs();
    }

    public void miscInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 640);
                isFullscreen = false;
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                isFullscreen = true;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void playerInputs() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.translateY(delta * playerSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.translateX(delta * -playerSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.translateY(delta * -playerSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.translateX(delta * playerSpeed);
        }
    }

    public void logic() {
        // Process game logic here

    }

    public void draw() {
        // Draw frame here
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        player.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    
}
