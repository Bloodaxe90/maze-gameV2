package io.github.eng1group9.entities;

import java.util.List;

import io.github.eng1group9.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles everything connected to the player.
 * @param startPos The players start positon. 
 * @author Mat and Max.
 */
public class Player extends MovingEntity {

    private boolean hasExitKey = false;
    private boolean hasChestRoomKey = false;

    public Player(Vector2 startPos, float speed) {
        super(new Texture("Characters/playerAnimations.png"), new int[] {4, 4,4,4} , 32, 32, speed);
        setPosition(startPos);
        setScale(2);
    }

    /**
     * Move the player if directional keys are being pressed.
     * @param worldCollision A list of rectangles the player cannot walk through.
     */
    public void handleInputs(List<Rectangle> worldCollision) {
        if (!isFrozen()) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                move('U', worldCollision);
                changeAnimation(1);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                move('L', worldCollision);
                changeAnimation(3);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                move('D', worldCollision);
                changeAnimation(0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                move('R', worldCollision);
                changeAnimation(2);
            }
        }
    }

    

    public boolean hasExitKey() {
        return hasExitKey;
    }

    public void setHasExitKey(Boolean bool) {
        System.out.println("Got exit key");
        hasExitKey = bool;
    }
    public boolean hasChestRoomKey() {
        return hasChestRoomKey;
    }

    public void setHasChestRoomKey(Boolean bool) {
        if (bool) {
            System.out.println("Got key");
            hasChestRoomKey = bool;
            Main.instance.deleteKeyTile();
        }
    }
}
