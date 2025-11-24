package io.github.game.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.game.Game;
import io.github.game.entities.MovableEntity;
import io.github.game.ui.elements.Hotbar;
import io.github.game.ui.elements.Item;
import io.github.game.utils.io.AudioPlayer;


public class Player extends MovableEntity {

    private boolean movingUp, movingDown, movingLeft, movingRight;
    private boolean interact = false;

    private final Array<Item> inventory;
    private float footstepTimer = 0;
    private final float footstepTimeout;


    public Player(RectangleMapObject properties, TextureAtlas spriteAtlas) {
        super(properties, spriteAtlas);
        this.inventory = new Array<>(Hotbar.NUM_SLOTS);
        this.footstepTimeout = 1 / getStartingProperty("footstepFrequency", Float.class);

    }


    public void render(SpriteBatch batch) {
        super.render(batch);
    }


    public void update(float delta_t, Game game) {
        super.update(delta_t, game);

        if (!active) return;
        Vector2 velocity = new Vector2(0f, 0f);
        if (movingLeft) velocity.x = -speed;
        if (movingRight) velocity.x = speed;
        if (movingUp) velocity.y = speed;
        if (movingDown) velocity.y = -speed;

        float newX = position.x + (velocity.x * delta_t);
        float newY = position.y + (velocity.y * delta_t) ;

        if (!isCollidable()) {
            setXPos(newX);
            setYPos(newY);
            return;
        }

        float oldX = position.x;
        float oldY = position.y;

        setXPos(newX);
        if (position.x < 0 || position.x + size.x > Game.WORLD_SIZE.x) {
            velocity.x = 0;
            setXPos(MathUtils.clamp(position.x, 0, Game.WORLD_SIZE.x - size.x));
        } else if (game.getEnvironmentManager().checkCollision(this) || game.getEntityManager().checkCollision(this)) {
            velocity.x = 0;
            setXPos(oldX);
        }

        setYPos(newY);
        if (position.y < 0 || position.y + size.y > Game.WORLD_SIZE.y) {
            velocity.y = 0;
            setYPos(MathUtils.clamp(position.y, 0, Game.WORLD_SIZE.y - size.y));
        } else if (game.getEnvironmentManager().checkCollision(this) || game.getEntityManager().checkCollision(this)) {
                        velocity.y = 0;
            setYPos(oldY);
        }

        updateSprite(velocity);
        updateSFX(delta_t, velocity);
    }


    private void updateSFX(float delta_t, Vector2 velocity) {

    if (velocity.isZero()) {
            footstepTimer = 0;
        } else {
        if (footstepTimer > footstepTimeout || footstepTimer == 0) {
                AudioPlayer.playSound("footstep" + MathUtils.random(1, 3), 0.5f, MathUtils.random(0.5f, 3f));
                footstepTimer = 0;
            }
            footstepTimer += delta_t;
        }
    }

    public void addItem(String itemName) {
        if (inventory.size < Hotbar.NUM_SLOTS) {
            inventory.add(new Item(itemName));
        }
    }

    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) return true;
        }
        return false;
    }

    public void removeItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) inventory.removeValue(item, false);
        }
    }

    public void setMovingUp(boolean movingUp) { this.movingUp = movingUp; }
    public void setMovingDown(boolean movingDown) { this.movingDown = movingDown; }
    public void setMovingLeft(boolean movingLeft) { this.movingLeft = movingLeft; }
    public void setMovingRight(boolean movingRight) { this.movingRight = movingRight; }
    public void setInteract(boolean interact) {this.interact = interact; }

    public Array<Item> getInventory() { return inventory; }


    public void stopMoving() {
        movingLeft = movingRight = movingUp = movingDown = false;
    }
}
