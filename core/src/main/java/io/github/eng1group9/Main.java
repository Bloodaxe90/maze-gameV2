package io.github.eng1group9;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Texture image;

    private FitViewport viewport;


    @Override
    public void create() {
        batch = new SpriteBatch();
        testMap = new TmxMapLoader().load("World/testMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(testMap);
        camera.setToOrtho(false, 960, 960);
        camera.update();
        viewport = new FitViewport(960, 960, camera);
        
        image = new Texture("World/testTileset.png");
        

    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    public void input() {
        // Process player inputs here
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 960);
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
        batch.draw(image, 0, 0);
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
