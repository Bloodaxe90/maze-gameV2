package io.github.eng1group9;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.eng1group9.entities.*;
import io.github.eng1group9.toasts.ToastManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpriteBatch UI;
    private Texture missingTexture;

    private boolean isFullscreen = false;
    private boolean isPaused = false;

    private TiledMap testMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera = new OrthographicCamera();

    private FitViewport viewport;
    private long elapsedTime = 0;
    private boolean showCollision = false;

    private List<Rectangle> worldCollision;
    private List<Rectangle> exitDoorCollision;

    private Player player;
    final Vector2 PLAYERSTARTPOS = new Vector2(16, 532);
    final float DEFAULTPLAYERSPEED = 100;

    private Dean dean;
    final Vector2 DEANSTARTPOS = new Vector2(32, 352);
    final float DEFAULTDEANSPEED = 100;
    final Character[] DEANPATH = {
        'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R', 'R',
        'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
        'R', 'R', 'R',
        'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U', 'U',
        'L', 'L', 'L',
        'D', 'D', 'D', 'D',
        'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'L'
    };


    private Chest chest;

    public static Main instance;

    @Override
    public void create() {
        missingTexture = new Texture("missingTexture.png");
        batch = new SpriteBatch();
        UI = new SpriteBatch();
        setupWorld();
        setupWorldCollision();
        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEANPATH);
        chest = new Chest();
        instance = this;
    }

    public void setupWorld() {
        testMap = new TmxMapLoader().load("World/testMap.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(testMap);
        camera.setToOrtho(false, 480, 320);
        camera.update();
        viewport = new FitViewport(480, 320, camera);
    }

    public void deleteKeyTile() {
        TiledMapTileLayer layer = (TiledMapTileLayer) testMap.getLayers().get("key");

        // i = 1; j = 7
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                Cell cell = layer.getCell(i, j);

                if (cell != null) {
                    cell.setTile(null);
                }
            }
        }
    }

    @Override
    public void render() {
        checkForKey();
        checkForNearChestRoomDoorWithKey();
        input();
        logic();
        draw();
    }

    public void checkForKey() {
        float playerX = player.getX();
        float playerY = player.getY();

        if (((playerX - 17) * (playerX - 17)) + ((playerY - 223) * (playerY - 223)) < 50) {
            player.setHasChestRoomKey(true);
        }
    }

    // Serches collision layer for name
    // Then deletes from world collision by the rectangle
    private void removeCollisionByName(String name) {
        MapLayer collisionLayer = (MapLayer) testMap.getLayers().get("Collision");
        MapObjects collisionObjects = collisionLayer.getObjects();

        for (MapObject m : collisionObjects) {

            if (m.getName() == null) {
                continue;
            }

            if (!m.getName().equals(name)) {
                continue;
            }

            Rectangle r = ((RectangleMapObject) m).getRectangle();
            worldCollision.remove(r);
        }
    }

    private void checkForNearChestRoomDoorWithKey() {
        float playerX = player.getX();
        float playerY = player.getY();

        if (((playerX - 238) * (playerX - 238)) + ((playerY - 353) * (playerY - 353)) < 50) {
            if (player.hasChestRoomKey()) {
                ToastManager.addToast("You opened the door");
                removeCollisionByName("chestRoomDoor");
            }
        }
    }

    public void input() {
        // Process user inputs here
        miscInputs();

        if (player.hasExitKey()) {
            player.handleInputs(worldCollision);
        } else {
            player.handleInputs(Stream.concat(worldCollision.stream(), exitDoorCollision.stream()).collect(Collectors.toList()));
        }
    }

    public void miscInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit(); // Close the game
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            showCollision = !showCollision;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            tryInteract();
        }
    }


    private void tryInteract() {
        if (!chest.opened) {
            if (chest.distanceTo(player) < 50) {
                player.setHasExitKey(true);
                chest.open();
            }
        }
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 640);
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            isFullscreen = !isFullscreen;
    }

    private void togglePause() {
        if (isPaused) {
            player.unfreeze();
            dean.unfreeze();
        }
        else {
            player.freeze();
            dean.freeze();
        }
        isPaused = !isPaused;
    }

    private void setupWorldCollision() {
        MapLayer collisionLayer = (MapLayer) testMap.getLayers().get("Collision");
        MapObjects collisionObjects = collisionLayer.getObjects();
        worldCollision = new LinkedList<>();
        for (MapObject mapObject : collisionObjects) {
            Rectangle nextRectangle = ((RectangleMapObject) mapObject).getRectangle();
            nextRectangle.set(nextRectangle.x * 2,nextRectangle.y * 2, nextRectangle.width * 2, nextRectangle.height * 2);
            worldCollision.add(nextRectangle);
        }


        MapLayer collisionLayer2 = (MapLayer) testMap.getLayers().get("exitdoor collision");
        MapObjects collisionObjects2 = collisionLayer2.getObjects();
        exitDoorCollision = new LinkedList<>();
        for (MapObject mapObject : collisionObjects2) {
            Rectangle nextRectangle = ((RectangleMapObject) mapObject).getRectangle();
            nextRectangle.set(nextRectangle.x * 2,nextRectangle.y * 2, nextRectangle.width * 2, nextRectangle.height * 2);
            exitDoorCollision.add(nextRectangle);
        }
    }


    public void logic() {
        // Process game logic here
        float delta = Gdx.graphics.getDeltaTime();
        if (!isPaused) elapsedTime += (delta * 1000);
        dean.nextMove(worldCollision);
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
        int[] belowPlayer = {0, 1, 2}; // the layers which should appear below the player
        mapRenderer.render(belowPlayer);

        batch.begin();
        player.draw(batch);
        dean.draw(batch);





        batch.end();
        // Overlay text - must be before batch.end.

        int[] abovePlayer = {3, 4, 5, 6, 7, 8}; // the layers which should appear above the player
        mapRenderer.render(abovePlayer);

        UI.begin();
        BitmapFont font = new BitmapFont();
        font.draw(UI, "Time left: " + getClock(), 10, 640 - 10);


        ToastManager.clearExpiredToasts();
        List<String> toastTexts = ToastManager.getToasts();
        int offset = 0;

        for (String text : toastTexts) {
            offset += 30;
            font.draw(UI, text, 10, (640 - 10) - offset);
        }

        if (showCollision) { // show collisions for debugging
            renderCollision();
        }
        UI.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        testMap.dispose();
    }

    public void renderCollision() {
        for (Rectangle rectangle : worldCollision) {
                UI.draw(missingTexture, rectangle.x, rectangle.y , rectangle.width, rectangle.height);
            }
            UI.draw(missingTexture, player.getHitbox().x + 16, player.getHitbox().y+ 16, player.getHitbox().width, player.getHitbox().height);
            UI.draw(missingTexture, dean.getHitbox().x + 16, dean.getHitbox().y+ 16, dean.getHitbox().width, dean.getHitbox().height);
            chest.draw(UI);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }


}
