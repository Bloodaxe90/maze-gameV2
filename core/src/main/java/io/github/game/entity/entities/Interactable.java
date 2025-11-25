package io.github.game.entity.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import io.github.game.Game;
import io.github.game.entity.Entity;

public class Interactable extends Entity {

    private String currentSpriteKey;

    public Interactable(RectangleMapObject properties, TextureAtlas spriteAtlas) {
        super(properties, spriteAtlas);
    }

    @Override
    public void update(float delta_t, Game game) {
        super.update(delta_t, game);
        setSprite(currentSpriteKey);
    }

    @Override
    public void setSprite(String name) {
        super.setSprite(name);
        this.currentSpriteKey = name;
    }
}
