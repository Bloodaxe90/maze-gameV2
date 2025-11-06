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
import io.github.eng1group9.systems.TriggerSystem;
import io.github.eng1group9.systems.TimerSystem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    boolean isFullscreen = false;
    static int gameState = 0;
    /// 0 = Not started
    /// 1 = Playing
    /// 2 = Paused
    /// 3 = Win
    /// 4 = Lose

    public static boolean chestDoorOpen = false;
    public static boolean exitOpen = false;
    public static boolean spikesLowered = false;

    private static TimerSystem timerSystem = new TimerSystem();
    public boolean showCollision = false;

    private List<Rectangle> worldCollision;
    final static int LONGBOIBONUS = 2000;
    final static String TMXPATH = "World/testMap.tmx";

    public static Player player;
    private static boolean playerCaught = false;
    private float playerCaughtTime = 2;
    final Vector2 PLAYERSTARTPOS = new Vector2(16, 532);
    final float DEFAULTPLAYERSPEED = 100;

    private static Dean dean;
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

    final static Color BAD = new Color(1,1,0,1);
    final static Color GOOD = new Color(0,1,1,1);


    private Chest chest;

    public static Main instance;
    public static RenderingSystem renderingSystem = new RenderingSystem();
    public static CollisionSystem collisionSystem = new CollisionSystem();
    public static InputSystem inputSystem = new InputSystem();

    @Override
    public void create() {
        renderingSystem.initWorld(TMXPATH, 480, 320);
        collisionSystem.init(renderingSystem.getMapRenderer().getMap());
        TriggerSystem.init(TMXPATH);
        worldCollision = collisionSystem.getWorldCollision();
        player = new Player(PLAYERSTARTPOS, DEFAULTPLAYERSPEED);
        dean = new Dean(DEANSTARTPOS, DEFAULTDEANSPEED, DEANPATH);
        chest = new Chest();
        togglePause();
        instance = this;
    }

    public void deleteKeyTile() {
        collisionSystem.deleteKeyTile();
    }

    @Override
    public void render() {
        input();
        if (gameState == 1) logic();
        draw();
    }

    public void draw() {
        renderingSystem.draw(player, dean, showCollision, timerSystem.elapsedTime, worldCollision);
        switch (gameState) {
            case 0:
                renderingSystem.renderStartOverlay(960, 640);
                break;
            case 2:
                renderingSystem.renderPauseOverlay(960, 640);
                break;
            case 3:
                renderingSystem.renderWinOverlay(960, 640, timerSystem.getTimeLeft(), calculateScore());
                break;
            case 4:
                renderingSystem.renderLoseOverlay(960, 640);
                break;
            default:
                break;
        }
    }

    public static void openChestRoomDoor() {
        if (player.hasChestRoomKey() && !chestDoorOpen) {
            ToastSystem.addToast("You Opened the Door!", GOOD);
            collisionSystem.removeCollisionByName("chestRoomDoor");
            RenderingSystem.hideLayer("ChestDoorClosed");
            chestDoorOpen = true;
        }
    }

    public static void openExit() {
        if (player.hasExitKey() && !exitOpen) {
            ToastSystem.addToast("You Opened the Exit!", GOOD);
            collisionSystem.removeCollisionByName("exitDoor");
            RenderingSystem.hideLayer("ExitClosed");
            exitOpen = true;
        }
    }

    public void input() {
        inputSystem.handle(player);
    }

    public static void startGame() {
        if (gameState == 0) {
            gameState = 2;
            togglePause();
        }
    }

    public static void winGame() {
        togglePause();
        gameState = 3;
    }

    public static void LoseGame() {
        togglePause();
        gameState = 4;
    }

    public static int calculateScore() {
        return (int)timerSystem.getTimeLeft() * 1000 + LONGBOIBONUS;
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

    public static void togglePause() {
        if (gameState == 2 && !playerCaught) {
            player.unfreeze();
            dean.unfreeze();
            gameState = 1;
        }
        else {
            player.freeze();
            dean.freeze();
            if (gameState == 1) gameState = 2;
        }
    }

    public void logic() {
        // Process game logic here
        timerSystem.tick();
        dean.nextMove();
        checkDeanCatch();
        TriggerSystem.checkTouchTriggers(player);
    }

    /**
     * Checks if the dean has caught the player, and punishes them if he has by removing 50s from time left. 
     */
    public void checkDeanCatch() {
        if (dean.canReach(player) && !playerCaught) {
            startPlayerCatch();
        }
        else if (playerCaught && gameState == 1) {
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

    public static void dropSpikes() {
        if (!spikesLowered) {
            collisionSystem.removeCollisionByName("chestRoomSpikes");
            ToastSystem.addToast("You Lowered the Spikes!", GOOD);
            RenderingSystem.hideLayer("Spikes");
            RenderingSystem.hideLayer("Switch");
            spikesLowered = true;
        }
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
