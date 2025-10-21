package io.github.eng1group9;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;

    private boolean isFullscreen = false;
    private boolean isPaused = false;

    private TiledMap testMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera = new OrthographicCamera();

    private Texture playerSpriteSheet;
    private TextureRegion[][] playerFrames;
    private Sprite player;

    private FitViewport viewport;

    // Config
    private float playerSpeed = 100;

    private long elapsedTime = 0;
    private long lastFrameTime = System.currentTimeMillis();

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
        // Move this somewhere else?
        long frameTime = System.currentTimeMillis() - lastFrameTime;
        lastFrameTime = System.currentTimeMillis();

        if (!isPaused) elapsedTime += frameTime;

        input();
        logic();
        draw();
    }

    public void input() {
        // Process user inputs here
        if (!isPaused) playerInputs();
        miscInputs();
    }

    public void miscInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 640);
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }

            isFullscreen = !isFullscreen;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            isPaused = !isPaused;
        }
    }

    // Returns true if the player can move there
    private boolean checkCollision(float x, float y) {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) testMap.getLayers().get(0);

        int startX = (int) (x / 32);
        int startY = (int) (y / 32);
        int endX = (int) ((x + 32) / 32);
        int endY = (int) ((y + 32) / 32);

        for (int checkY = startY; checkY <= endY; checkY++) {
            for (int checkX = startX; checkX <= endX; checkX++) {
                Cell cell = collisionLayer.getCell(checkX, checkY);

                if (cell == null) {
                    return false;
                }

                int tileId = cell.getTile().getId();

                if (tileId == 32 || tileId == 33 || tileId == 34) {
                    return false;
                }
            }
        }


        return true;
    }

    private void playerInputs() {
        float nextIntendedX = player.getX();
        float nextIntendedY = player.getY();


        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            nextIntendedY +=  delta * playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            nextIntendedX += delta * -playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            nextIntendedY += delta * -playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            nextIntendedX +=  delta * playerSpeed;
        }

        if (checkCollision(nextIntendedX, nextIntendedY)) {
            player.setX(nextIntendedX);
            player.setY(nextIntendedY);
        }
    }

    public void logic() {
        // Process game logic here

    }

    public String getClock() {
        return Integer.toString(500 - (int)(elapsedTime / 1000));
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

        // Overlay text - must be before batch.end.
        BitmapFont font = new BitmapFont();
        font.draw(batch, getClock(), 10, 640 - 10);

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
