package io.github.eng1group9.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.eng1group9.entities.Dean;
import io.github.eng1group9.entities.Player;
import io.github.eng1group9.systems.ToastSystem;

import java.util.List;

public class RenderingSystem {
    private Texture missingTexture;
    private SpriteBatch worldBatch;
    private SpriteBatch uiBatch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthogonalTiledMapRenderer mapRenderer;

    public void initWorld(String tmxPath, int viewportWidth, int viewportHeight) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, viewportWidth, viewportHeight);
        this.camera.update();
        this.viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        TiledMap map = new TmxMapLoader().load(tmxPath);
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.missingTexture = new Texture("missingTexture.png");
        this.worldBatch = new SpriteBatch();
        this.uiBatch = new SpriteBatch();
        this.font = new BitmapFont();
    }

    public OrthogonalTiledMapRenderer getMapRenderer() { return mapRenderer; }

    public String getClock(long elapsedTime) {
        return Integer.toString(500 - (int)(elapsedTime / 1000));
    }


    public void draw(Player player, Dean dean, boolean showCollision, long elapsedTime, List<Rectangle> worldCollision) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        camera.update();
        mapRenderer.setView(camera);
        int[] belowPlayer = {0, 1, 2}; // the layers which should appear below the player
        mapRenderer.render(belowPlayer);

        worldBatch.begin();
        player.draw(worldBatch);
        dean.draw(worldBatch);
        worldBatch.end();

        int[] abovePlayer = {3, 4, 5, 6, 7, 8}; // the layers which should appear above the player
        mapRenderer.render(abovePlayer);

        uiBatch.begin();
        font.draw(uiBatch, "Time left: " + getClock(elapsedTime), 10, 640 - 10);

        renderToasts(font, uiBatch);

        if (showCollision && worldCollision != null) { // show collisions for debugging
            renderCollision(uiBatch, worldCollision, player, dean);
        }
        uiBatch.end();
    }

    public void renderToasts(BitmapFont font, SpriteBatch uiBatch) {
        ToastSystem.clearExpiredToasts();
        List<String> toastTexts = ToastSystem.getToasts();
        int offset = 0;

        for (String text : toastTexts) {
            offset += 30;
            font.draw(uiBatch, text, 10, (640 - 10) - offset);
        }
    }

    public void renderCollision(SpriteBatch uiBatch, List<Rectangle> worldCollision, Player player, Dean dean) {
        for (Rectangle rectangle : worldCollision) {
            uiBatch.draw(missingTexture, rectangle.x, rectangle.y , rectangle.width, rectangle.height);
        }
        uiBatch.draw(missingTexture, player.getHitbox().x + 16, player.getHitbox().y+ 16, player.getHitbox().width, player.getHitbox().height);
        uiBatch.draw(missingTexture, dean.getHitbox().x + 16, dean.getHitbox().y+ 16, dean.getHitbox().width, dean.getHitbox().height);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void renderPauseOverlay(int screenWidth, int screenHeight) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.5f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.draw(uiBatch, "Max's Maze", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.draw(uiBatch, "Instructions", screenWidth / 2f, (screenHeight / 2f) - 100);
        font.getData().setScale(1f);
        font.draw(uiBatch, "Press P to resume!", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 20);
        font.draw(uiBatch, "Press E to interact.", screenWidth / 2f, (screenHeight / 2f) - 40);
        font.draw(uiBatch, "Use WASD to move.", screenWidth / 2f, (screenHeight / 2f) - 60);
        font.draw(uiBatch, "Avoid the dean and escape the maze in time!", screenWidth / 2f, (screenHeight / 2f) - 140);

        uiBatch.end();
    }
}
