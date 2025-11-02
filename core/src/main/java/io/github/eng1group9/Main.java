package io.github.eng1group9;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.eng1group9.entities.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;

    private boolean isFullscreen = false;
    private boolean isPaused = false;

    private TiledMap testMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera = new OrthographicCamera();

    private FitViewport viewport;
    private float elapsedTime = 0;

    private Player player;
    private float playerSpeed = 100;
    final Vector2 PLAYERSTARTPOS = new Vector2(16, 532);

    private Dean dean;
    private float deanSpeed = 10;
    final Vector2 DEANSTARTPOS = new Vector2(16, 532);






    @Override
    public void create() {
        batch = new SpriteBatch();
        setupWorld();
        player = new Player(PLAYERSTARTPOS);
        dean = new Dean(DEANSTARTPOS);
    }

    public void setupWorld() {
        testMap = new TmxMapLoader().load("World/testMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(testMap);
        camera.setToOrtho(false, 480, 320);
        camera.update();
        viewport = new FitViewport(480, 320, camera);
    }

    @Override
    public void render() {
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
            player.playAnimation(1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            nextIntendedX += delta * -playerSpeed;
            player.playAnimation(3);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            nextIntendedY += delta * -playerSpeed;
            player.playAnimation(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            nextIntendedX +=  delta * playerSpeed;
            player.playAnimation(2);
        }

        if (checkCollision(nextIntendedX, nextIntendedY)) {
            player.setX(nextIntendedX);
            player.setY(nextIntendedY);
        }
    }

    public void logic() {
        // Process game logic here
        float delta = Gdx.graphics.getDeltaTime();
        if (!isPaused) elapsedTime += (delta * 1000);
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
        dean.draw(batch);

        // Overlay text - must be before batch.end.
        BitmapFont font = new BitmapFont();
        font.draw(batch, getClock(), 10, 640 - 10);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        testMap.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }


}
