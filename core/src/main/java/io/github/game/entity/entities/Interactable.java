package io.github.game.entity.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

import io.github.game.Game;
import io.github.game.entity.Entity;

/**
 * A simple entity class for objects in the world that can change their sprite,
 * like a switch that can be turned on or off
 */
public class Interactable extends Entity {

    // Stores the name of the current sprite being used
    private String currentSpriteKey;

    /**
     * Constructor for an Interactable entity
     * @param properties The rectangle object from the Tiled map
     * @param spriteAtlas The texture atlas for this entities sprites
     */
    public Interactable(RectangleMapObject properties, TextureAtlas spriteAtlas) {
        super(properties, spriteAtlas);

        hitbox.setHeight(hitbox.height * 1.5f);
    }

    @Override
    public void update(float delta_t, Game game) {
        super.update(delta_t, game);

        // This makes sure the sprite is constantly updated to the correct animation frame
        // based on the stateTime, which is incremented in the parent's update method
        setSprite(currentSpriteKey);
    }

    @Override
    public void setSprite(String name) {
        // We override the parent's setSprite method so we can keep track
        // of the current animation's name
        super.setSprite(name);
        this.currentSpriteKey = name;
    }
}
