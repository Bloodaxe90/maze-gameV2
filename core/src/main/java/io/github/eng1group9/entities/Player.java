package io.github.eng1group9.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Handles everything connect to the player.
 * @author Mat & max.
 */
public class Player extends AnimatedEntity {

    private float speed;
    private boolean hasKey = false;

    public Player(Vector2 startPos) {
        super(new Texture("Characters/playerAnimations.png"), new int[] {4, 4,4,4} , 32, 32);
        setPosition(startPos);
        setScale(2);
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setHasKey(Boolean bool) {
        hasKey = bool;
    }
}
