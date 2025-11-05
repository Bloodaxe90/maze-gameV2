package io.github.eng1group9;

import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.entities.*;
import io.github.eng1group9.systems.InputSystem;
import io.github.eng1group9.systems.RenderingSystem;
import io.github.eng1group9.systems.CollisionSystem;
import io.github.eng1group9.systems.ToastSystem;
import io.github.eng1group9.systems.TimerSystem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    boolean isFullscreen = false;
    boolean isPaused = false;
    boolean gameStarted = false;

    public static boolean chestDoorOpen = false;
    public static boolean mainDoorOpen = false;

    private TimerSystem timerSystem = new TimerSystem();
    public boolean showCollision = false;

    private List<Rectangle> worldCollision;

    public Player player;
    private boolean playerCaught = false;
    private float playerCaughtTime = 2;
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

    final Color BAD = new Color(1,1,0,1);
    final Color GOOD = new Color(0,1,1,1);


    private Chest chest;

    public static Main instance;
    public static RenderingSystem renderingSystem = new RenderingSystem();
    public static CollisionSystem collisionSystem = new CollisionSystem();
    public static InputSystem inputSystem = new InputSystem();

    @Override
    public void create() {
        renderingSystem.initWorld("World/testMap.tmx", 480, 320);
        collisionSystem.init(renderingSystem.getMapRenderer().getMap());
        worldCollision = collisionSystem.getWorldCollision();

        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEANPATH);
        chest = new Chest();
        togglePause(); // so the game starts paused
        instance = this;
    }

    public void deleteKeyTile() {
        collisionSystem.deleteKeyTile();
    }

    @Override
    public void render() {
        input();
        if (!isPaused) logic();
        draw();
    }

    public void draw() {
        renderingSystem.draw(player, dean, showCollision, timerSystem.elapsedTime, worldCollision);
        if (isPaused && gameStarted) {
            renderingSystem.renderPauseOverlay(960, 640);
        }
        else if (!gameStarted) {
            renderingSystem.renderStartOverlay(960, 640);
        }
    }

    public void checkForKey() {
        float playerX = player.getX();
        float playerY = player.getY();

        if (((playerX - 17) * (playerX - 17)) + ((playerY - 223) * (playerY - 223)) < 50) {
            player.giveChestRoomKey();
        }
    }

    private void checkForNearChestRoomDoorWithKey() {
        float playerX = player.getX();
        float playerY = player.getY();

        if (((playerX - 238) * (playerX - 238)) + ((playerY - 353) * (playerY - 353)) < 50) {
            if (player.hasChestRoomKey() && !chestDoorOpen) {
                ToastSystem.addToast("You opened the door");
                collisionSystem.removeCollisionByName("chestRoomDoor");
                collisionSystem.hideLayer("ChestDoorClosed");
                chestDoorOpen = true;
            }
        }
    }

    public void input() {
        inputSystem.handle(player);
    }

    public void startGame() {
        if (!gameStarted) {
            gameStarted = true;
            togglePause();
        }
    }

    public void tryInteract() {
        if (!chest.opened) {
            if (chest.distanceTo(player) < 50) {
                player.giveExitKey();
                chest.open();
            }
        }
    }

    public void toggleFullscreen() {
        if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 640);
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            isFullscreen = !isFullscreen;
    }

    public void togglePause() {
        if (isPaused && !playerCaught && gameStarted) {
            player.unfreeze();
            dean.unfreeze();
        }
        else {
            player.freeze();
            dean.freeze();
        }
        isPaused = !isPaused;
    }

    public void logic() {
        // Process game logic here
        timerSystem.tick();
        dean.nextMove();
        checkForKey();
        checkForNearChestRoomDoorWithKey();
        checkDeanCatch();
    }

    /**
     * Checks if the dean has caught the player, and punishes them if he has by removing 50s from time left. 
     */
    public void checkDeanCatch() {
        if (dean.canReach(player) && !playerCaught) {
            startPlayerCatch();
        }
        else if (playerCaught && !isPaused) {
            if (playerCaughtTime <= 0) {
                endPlayerCatch();
                playerCaughtTime = 2;
            }
            else {
                float delta = Gdx.graphics.getDeltaTime();
                playerCaughtTime -= delta;
            }
        }
    }

    private void startPlayerCatch() {
        player.freeze();
        player.setPosition(PLAYERSTARTPOS);
        dean.freeze();
        dean.changeAnimation(3);
        dean.setPosition(PLAYERSTARTPOS.x + 32, PLAYERSTARTPOS.y);
        playerCaught = true;
        timerSystem.addGradually(48000); // 48 not 50 because you spend 2s stood while the timer goes down.
        ToastSystem.addToast("You were caught by the Dean!", BAD);
        ToastSystem.addToast("You were stuck being lectured for 50s!", BAD);
    }

    private void endPlayerCatch() {
        dean.setPosition(DEANSTARTPOS);
        dean.restartPath();
        dean.unfreeze();
        player.unfreeze();
        playerCaught = false;
    }

    @Override
    public void resize(int width, int height) {
        renderingSystem.resize(width, height);
    }

    @Override
    public void pause() {
        togglePause();
    }

    @Override
    public void resume() {
        togglePause();
    }
}
