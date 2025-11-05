package io.github.eng1group9;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.entities.*;
import io.github.eng1group9.systems.RenderingSystem;
import io.github.eng1group9.systems.ToastSystem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    private boolean isFullscreen = false;
    private boolean isPaused = false;

    private TiledMap testMap;
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
    public static RenderingSystem renderingSystem = new RenderingSystem();

    @Override
    public void create() {
        renderingSystem.initWorld("World/testMap.tmx", 480, 320);
        testMap = renderingSystem.getMapRenderer().getMap();

        setupWorldCollision();

        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEANPATH);
        chest = new Chest();

        instance = this;
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
        input();
        logic();
        renderingSystem.draw(player, dean, showCollision, elapsedTime, worldCollision);
        if (isPaused) {
            renderingSystem.renderPauseOverlay(960, 640);
        }
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
                ToastSystem.addToast("You opened the door");
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
        if (!isPaused) elapsedTime += (long) (delta * 1000);
        dean.nextMove(worldCollision);
        checkForKey();
        checkForNearChestRoomDoorWithKey();
    }

    @Override
    public void resize(int width, int height) {
        renderingSystem.resize(width, height);
    }
}
