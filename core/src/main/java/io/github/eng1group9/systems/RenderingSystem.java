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
import io.github.eng1group9.systems.ToastSystem.Toast;
import io.github.eng1group9.systems.TriggerSystem.Trigger;

import java.util.List;

public class RenderingSystem {
    private Texture missingTexture;
    private SpriteBatch worldBatch;
    private SpriteBatch uiBatch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthogonalTiledMapRenderer mapRenderer;
    private static TiledMap map;

    public void initWorld(String tmxPath, int viewportWidth, int viewportHeight) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, viewportWidth, viewportHeight);
        this.camera.update();
        this.viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        map = new TmxMapLoader().load(tmxPath);
        this.mapRenderer = new OrthogonalTiledMapRenderer(map);
        this.missingTexture = new Texture("missingTexture.png");
        this.worldBatch = new SpriteBatch();
        this.uiBatch = new SpriteBatch();
        this.font = new BitmapFont();
    }

    public OrthogonalTiledMapRenderer getMapRenderer() { return mapRenderer; }

    public String getClock(float elapsedTime) {
        return Integer.toString(500 - (int)(elapsedTime / 1000));
    }

    public static void hideLayer(String name) {
        map.getLayers().get(name).setVisible(false);
    }

    public static void showLayer(String name) {
        map.getLayers().get(name).setVisible(true);
    }

    public void draw(Player player, Dean dean, boolean showCollision, float elapsedTime, List<Rectangle> worldCollision) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        camera.update();
        mapRenderer.setView(camera);
        int[] belowPlayer = {0, 1, 2, 3, 4, 5, 6}; // the layers which should appear below the player
        mapRenderer.render(belowPlayer);

        worldBatch.begin();
        player.draw(worldBatch);
        dean.draw(worldBatch);
        worldBatch.end();

        int[] abovePlayer = {7, 8, 9, 10, 11, 12, 13, 14, 15}; // the layers which should appear above the player
        mapRenderer.render(abovePlayer);
        uiBatch.begin();
        font.draw(uiBatch, "Time left: " + getClock(elapsedTime), 10, 640 - 10);

        renderToasts(font, uiBatch);

        if (showCollision && worldCollision != null) { // show collisions for debugging
            renderCollision(uiBatch, worldCollision, player, dean);
            renderTriggers(uiBatch);
        }
        uiBatch.end();
    }

    public void renderToasts(BitmapFont font, SpriteBatch uiBatch) {
        ToastSystem.clearExpiredToasts();
        List<Toast> toasts = ToastSystem.getToasts();
        int offset = 0;

        for (Toast t : toasts) {
            offset += 30;
            font.setColor(t.getColour());
            font.draw(uiBatch, t.getText(), 10, (640 - 10) - offset);
        }
        font.setColor(1, 1, 1, 1);
    }

    public void renderCollision(SpriteBatch uiBatch, List<Rectangle> worldCollision, Player player, Dean dean) {
        for (Rectangle rectangle : worldCollision) {
            uiBatch.setColor(1, 0, 0, 0.75f);
            uiBatch.draw(missingTexture, rectangle.x, rectangle.y , rectangle.width, rectangle.height);
        }
        uiBatch.draw(missingTexture, player.getHitbox().x + 16, player.getHitbox().y+ 16, player.getHitbox().width, player.getHitbox().height);
        uiBatch.draw(missingTexture, dean.getReachRectangle().x + 16, dean.getReachRectangle().y+ 16, dean.getReachRectangle().width, dean.getReachRectangle().height);
    }

    public void renderTriggers(SpriteBatch uiBatch) {
        for (Trigger t : TriggerSystem.getTriggers()) {
            Rectangle rectangle = t.getZone();
            uiBatch.setColor(0, 1, 1, 0.75f);
            uiBatch.draw(missingTexture, rectangle.x, rectangle.y , rectangle.width, rectangle.height);
        }
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
        font.draw(uiBatch, "Escape from Uni", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.draw(uiBatch, "Instructions", screenWidth / 2f, (screenHeight / 2f) - 120);

        font.getData().setScale(1f);
        font.draw(uiBatch, "Press P to resume!", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 20);
        font.draw(uiBatch, "Press E to interact.", screenWidth / 2f, (screenHeight / 2f) - 40);
        font.draw(uiBatch, "Use WASD or arrow keys to move.", screenWidth / 2f, (screenHeight / 2f) - 60);
        font.draw(uiBatch, "Press F11 to Fullscreen.", screenWidth / 2f, (screenHeight / 2f) - 80);
        font.draw(uiBatch, "Avoid the dean and escape the maze in time!", screenWidth / 2f, (screenHeight / 2f) - 160);
        
        uiBatch.end();
    }

    public void renderStartOverlay(int screenWidth, int screenHeight) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.5f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.draw(uiBatch, "Escape from Uni", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.draw(uiBatch, "Instructions", screenWidth / 2f, (screenHeight / 2f) - 120);

        font.setColor(0, 1, 1, 1);
        font.draw(uiBatch, "Press Space to Start!", screenWidth / 2f, (screenHeight / 2f) - 200);
        font.setColor(1, 1, 1, 1);

        font.getData().setScale(1f);
        font.draw(uiBatch, "Press P to pause!", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 20);
        font.draw(uiBatch, "Press E to interact.", screenWidth / 2f, (screenHeight / 2f) - 40);
        font.draw(uiBatch, "Use WASD or arrow keys to move.", screenWidth / 2f, (screenHeight / 2f) - 60);
        font.draw(uiBatch, "Press P to pause!", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press F11 to Fullscreen.", screenWidth / 2f, (screenHeight / 2f) - 80);
        font.draw(uiBatch, "Avoid the dean and escape the maze in time!", screenWidth / 2f, (screenHeight / 2f) - 160);
        

        uiBatch.end();
    }

    public void renderWinOverlay(int screenWidth, int screenHeight, float timeLeft, int Score) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.5f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.setColor(0, 1, 1, 1);
        font.draw(uiBatch, "YOU WIN!", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.setColor(1, 1, 1, 1);
        font.getData().setScale(1f);
        font.draw(uiBatch, "Time Left: " + timeLeft, screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Score: " + Integer.toString(Score), screenWidth / 2f, (screenHeight / 2f) - 20);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 40);
        uiBatch.end();
    }

    public void renderLoseOverlay(int screenWidth, int screenHeight) {
        uiBatch.begin();
        uiBatch.setColor(0, 0, 0, 0.5f);
        uiBatch.draw(missingTexture, 0, 0, screenWidth, screenHeight);
        uiBatch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.setColor(1, 0, 0, 1);
        font.draw(uiBatch, "TIME IS UP!", screenWidth / 2f, (screenHeight / 2f) + 40);
        font.setColor(1, 1, 1, 1);
        font.getData().setScale(1f);
        font.draw(uiBatch, "Better luck next time.", screenWidth / 2f, screenHeight / 2f);
        font.draw(uiBatch, "Press ESC to quit.", screenWidth / 2f, (screenHeight / 2f) - 40);
        uiBatch.end();
    }
}
