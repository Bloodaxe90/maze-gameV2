package io.github.eng1group9.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import io.github.eng1group9.systems.RenderingSystem;
import io.github.eng1group9.systems.ToastSystem;

/**
 * Handles everything connected to the player.
 * @param startPos The players start positon.
 * @author Mat and Max.
 */
public class Player extends MovingEntity {

    private boolean hasExitKey = false;
    private boolean hasChestRoomKey = false;
    private boolean hasRedPotion = false;
    private float invisibilityLeft = 0;
    private boolean invisibilityWarningGiven = true;

    public Player(Vector2 startPos, float speed) {
        super(new Texture("Characters/playerAnimations.png"), new int[] {4, 4, 4, 4, 4, 4, 4, 4} , 32, 32, speed);
        setPosition(startPos);
        setScale(2);
    }

    public boolean hasExitKey() {
        return hasExitKey;
    }

    public void giveExitKey() {
        if (!hasExitKey) {
            hasExitKey = true;
            ToastSystem.addToast("You found the Exit Key!");
        }
    }

    public boolean hasChestRoomKey() {
        return hasChestRoomKey;
    }

    public void giveChestRoomKey() {
        if (!hasChestRoomKey) {
            hasChestRoomKey = true;
            RenderingSystem.hideLayer("Key");
            ToastSystem.addToast("You found the Storage Room Key!");
        }
    }

    public void giveRedPotion() {
        if (!hasRedPotion) {
            hasRedPotion = true;
            RenderingSystem.hideLayer("Potion");
            ToastSystem.addToast("You found a Red Potion?");
        }
    }

    public boolean hasRedPotion() {
        return hasRedPotion;
    }

    public void becomeInvisible() {
        invisibilityLeft = 15;
        invisibilityWarningGiven = false;
    }
    

    @Override
    public float move(Character direction) {
        int animationOffset = 0;
        if (!isVisible()) animationOffset = 4;
        switch (direction) {
                case 'U':
                    changeAnimation(1 + animationOffset);
                    break;
                case 'D':
                    changeAnimation(0 + animationOffset);
                    break;
                case 'L':
                    changeAnimation(3 + animationOffset);
                    break;
                case 'R':
                    changeAnimation(2 + animationOffset);
                    break;
            }
        return super.move(direction);
    }

    public boolean isVisible() {
        return invisibilityLeft <= 0;
    }


    public void update() {

        if (!isVisible()) {
            invisibilityLeft -= Gdx.graphics.getDeltaTime();
            if (isVisible()) {
                ToastSystem.addToast("Your invisibility has run out!");
                changeAnimation(1);
            }
            
            if (invisibilityLeft <= 5 && !invisibilityWarningGiven) {
                ToastSystem.addToast("Your invisibility is about to run out!");
                invisibilityWarningGiven = true;
            }
            
        } 
    }

    
}
