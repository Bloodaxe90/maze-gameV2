package io.github.eng1group9;

import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

    public static boolean chestDoorOpen = false; // Wether the door to the chest room has been opened.
    public static boolean exitOpen = false; // Wether the exit is open/
    public static boolean spikesLowered = false; // Wether the spikes in the chest room have been lowered.
    public static boolean scrollUsed = false; // Wether the scroll power up has been collected.
    public static int longboiBonus = 0; // the bonus to add based on wether LongBoi was found. 

    private static TimerSystem timerSystem = new TimerSystem();
    public boolean showCollision = false;

    private List<Rectangle> worldCollision;
    final static int LONGBOIBONUSAMOUNT = 201823;
    final static String TMXPATH = "World/testMap.tmx";

    public static Player player;
    private static boolean playerCaught = false; // Wether the player is currently being held by the Dean.
    final float INITALPLAYERCAUGHTTIME = 2;
    private float playerCaughtTime = INITALPLAYERCAUGHTTIME; // how many seconds the Dean will hold the player when caught.
    final Vector2 PLAYERSTARTPOS = new Vector2(16, 532); // Where the player begins the game, and returns to when caught.
    final float DEFAULTPLAYERSPEED = 100; // The players speed. 

    private static Dean dean;
    final Vector2 DEANSTARTPOS = new Vector2(32, 352); // Where the Dean begins the game, and returns to after catching the player.
    final float DEFAULTDEANSPEED = 100;
    final int DEANPUNISHMENT = 50; // The number of seconds the Dean adds to the timer.
    final Character[] DEANPATH = { // The path the dean will take (D = Down, U = Up, L = Left, R = Right). The path will loop. 
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
        togglePause();
        instance = this;
    }

    @Override
    public void render() {
        inputSystem.handle(player);
        if (gameState == 1) logic();
        draw();
    }

    /**
     * Check if the player has the red potion.
     * If they do, show Long Boi and complete the hidden event. 
     * If not, tell the player they must find the potion. 
     */
    public static void checkForLongboi() {
        Color messageColour = new Color(0.2f, 1, 0.2f ,1);
        if (longboiBonus == 0 && !player.hasRedPotion()) {
            ToastSystem.addToast("Hello There! I seem to have misplaced my Red Potion, could you get it for me?", messageColour);
         }
        else if (longboiBonus == 0 && player.hasRedPotion()){
            longboiBonus = LONGBOIBONUSAMOUNT;
            ToastSystem.addToast("You found my potion! Thank you!", messageColour);
            RenderingSystem.showLayer("LONGBOI");
        }
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

    /**
     * Open the door of the room with the chest.
     * Remove its hitbox and graphic. 
     */
    public static void openChestRoomDoor() {
        if (player.hasChestRoomKey() && !chestDoorOpen) {
            ToastSystem.addToast("You Opened the Door!", GOOD);
            collisionSystem.removeCollisionByName("chestRoomDoor");
            RenderingSystem.hideLayer("ChestDoorClosed");
            chestDoorOpen = true;
        }
    }

    /**
     * Open the exit.
     * Remove its hitbox and hide its graphic. 
     */
    public static void openExit() {
        if (player.hasExitKey() && !exitOpen) {
            ToastSystem.addToast("You Opened the Exit!", GOOD);
            collisionSystem.removeCollisionByName("exitDoor");
            RenderingSystem.hideLayer("ExitClosed");
            exitOpen = true;
        }
    }

    /**
     * Give the scroll powerup to the player, making the player invisible for 15s.
     * Hide the scroll graphic. 
     */
    public static void getScroll() {
        if (!scrollUsed) {
            player.becomeInvisible();
            RenderingSystem.hideLayer("Scroll");
            scrollUsed = true;
            ToastSystem.addToast("You got the Scroll!", GOOD);
            ToastSystem.addToast("You are invisible for 15s", GOOD);
        }
    }

    /**
     * Set the game to the started state, and unpause it. 
     * Used once to start the game. 
     */
    public static void startGame() {
        if (gameState == 0) {
            gameState = 2;
            togglePause();
        }
    }

    /**
     * Set the game to the win state, used when the player escapes. 
     */
    public static void winGame() {
        togglePause();
        gameState = 3;
    }

    /**
     * Set the game to the lose state, used when the player runs out of time. 
     */
    public static void LoseGame() {
        togglePause();
        gameState = 4;
    }

    /**
     * Calculate the players score based on how much time was left and wether they found Long Boi. 
     * @return The score.
     */
    public static int calculateScore() {
        return TimerSystem.getTimeLeft() * 1000 + longboiBonus;
    }

    /**
     * Toggle wether the window should be in Windowed or Fullcreen mode. 
     */
    public void toggleFullscreen() {
        if (isFullscreen) {
                Gdx.graphics.setWindowedMode(960, 640);
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            isFullscreen = !isFullscreen;
    }

    /**
     * Toggle wether the game shoudl be paused. 
     * This will freeze the player/dean, stop all game logic and display the pause overlay. 
     */
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

    /**
     * Where the game process its logic each frame. 
     * It will not run if the game is paused. 
     */
    public void logic() {
        timerSystem.tick();
        dean.nextMove();
        checkDeanCatch();
        TriggerSystem.checkTouchTriggers(player);
        player.update();
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
                playerCaughtTime = INITALPLAYERCAUGHTTIME;
            }
            else {
                float delta = Gdx.graphics.getDeltaTime();
                playerCaughtTime -= delta;
            }
        }
    }

    /**
     * Run when the player is first caught by the game. 
     * This will begin the sequence where the player is held in detention while the timer goes down.
     * This will freeze the player and dean. 
     */
    private void startPlayerCatch() {
        player.freeze();
        player.setPosition(PLAYERSTARTPOS);
        dean.freeze();
        dean.changeAnimation(3);
        dean.setPosition(PLAYERSTARTPOS.x + 32, PLAYERSTARTPOS.y);
        playerCaught = true;
        timerSystem.addGradually(48); // 48 not 50 because you spend 2s stood while the timer goes down.
        ToastSystem.addToast("You were caught by the Dean!", BAD);
        ToastSystem.addToast("You were stuck being lectured for 50s!", BAD);
    }

    /**
     * This will end the sequence where the player is held in detention while the timer goes down.
     * This will unfreeze the player and dean.
     * It will rest the dean to the start of its patrol. 
     */
    private void endPlayerCatch() {
        dean.setPosition(DEANSTARTPOS);
        dean.restartPath();
        dean.unfreeze();
        player.unfreeze();
        playerCaught = false;
    }

    /**
     * Use to drop remove the spikes in the chest room when the switch is pressed. 
     */
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
