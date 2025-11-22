package io.github.game.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;


public abstract class MovableEntity extends Entity{

    protected float speed;
    protected String currentSpriteDirection = "front";


    public MovableEntity(String id,
                         String hostLayer,
                         float speed,
                         boolean collidable,
                         TextureAtlas spriteAtlas) {
        super(id, hostLayer, collidable);
        this.speed = speed;

                for (String str : new String[]{"front", "back", "left", "right", "idlefront", "idleback", "idleleft", "idleright"}) {
            addSprite(str, 0.1f, Animation.PlayMode.LOOP, spriteAtlas);
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
