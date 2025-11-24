package io.github.game.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;


public abstract class MovableEntity extends Entity{

    protected float speed;
    protected String currentSpriteDirection = "front";


    public MovableEntity(RectangleMapObject properties, TextureAtlas spriteAtlas) {
        super(properties, spriteAtlas);
        this.speed = getStartingProperty("speed", Float.class);

        for (String name : new String[]{"front", "back", "left", "right", "idlefront", "idleback", "idleleft", "idleright"}) {
            addSprite(name, 0.1f);
        }

        setSprite(currentSpriteDirection);     }


    public void updateSprite(Vector2 velocity) {
        String prefix = velocity.isZero() ? "idle" : "";
        if (velocity.x > 0) {
            currentSpriteDirection = "right";
        } else if (velocity.x < 0) {
            currentSpriteDirection = "left";
        } else if (velocity.y > 0) {
            currentSpriteDirection = "back";
        } else if (velocity.y < 0) {
            currentSpriteDirection = "front";
        }
        setSprite(prefix + currentSpriteDirection);
    }


    public float getSpeed() {
        return speed;
    }
}
