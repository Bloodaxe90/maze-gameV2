package io.github.game.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.game.Game;
import io.github.game.utils.interactions.Trigger;
import io.github.game.utils.io.AnimationLoader;
import io.github.game.utils.io.MapLoader;


public abstract class Entity {

    protected String id;
    private RectangleMapObject startingProperties;
    protected Vector2 position;
    protected Vector2 size;

    protected Rectangle hitbox;
    protected boolean collidable;

    protected Map<String, Animation<TextureRegion>> spriteMap = new HashMap<>();
    protected float stateTime = 0f;
    protected TextureRegion sprite;

    protected Trigger trigger;
    protected boolean triggered;

    protected boolean active = true;

    public Entity(String id,
                  String hostLayer,
                  Trigger trigger,
                  boolean collidable) {

        this.id = id;
        this.startingProperties = MapLoader.getLayerRectangle(id, hostLayer);
        this.trigger = trigger;
        try {
            assert startingProperties != null;
            Rectangle startArea = startingProperties.getRectangle();
            this.hitbox = new Rectangle(
                startArea.x + (startArea.width / 4f),
                startArea.y,
                startArea.width / 2f,
                startArea.height / 2f
            );
            this.position = new Vector2(startArea.x, startArea.y);
            this.size = new Vector2(startArea.width, startArea.height);
        } catch (NullPointerException e) {
            Gdx.app.log("ERROR", String.valueOf(e));
        }

        this.collidable = collidable;
    }

    public Entity(String id,
                  String hostLayer,
                  boolean collidable) {

        this(id, hostLayer, null, collidable);
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(sprite, position.x, position.y, size.x, size.y);
        }
    };

    public void update(float delta_t, Game game) {
        if (active) {
            stateTime += delta_t;
            tryTrigger(game);
        }
    };

    private void tryTrigger(Game game) {
        if (triggered && trigger != null) {
            trigger.trigger(game);
        triggered = false;         }
    }

    public void addSprite(String name, float duration, Animation.PlayMode playMode, TextureAtlas spriteAtlas) {
        spriteMap.put(id + "_" + name, AnimationLoader.getAnimation(id + "_" + name, duration, spriteAtlas, playMode));
    }


    public void setSprite(String name) {
        sprite = new Sprite(spriteMap.get(id + "_" + name).getKeyFrame(stateTime));
    }


    public Vector2 getPos() {
        return new Vector2(position);
    }


    public void setXPos(Float XPos) {
        position.x = XPos;
        hitbox.setX(XPos + (size.y / 4f));     }


    public void setYPos(Float YPos) {
        position.y = YPos;
        hitbox.setY(YPos);     }


    public Vector2 getSize() {
        return size;
    }


    public String getId() {
        return id;
    }


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    public Rectangle getHitbox() {
        return hitbox;
    }

    protected <T> T getStartingProperty(String propertyName, Class<T> type) {
        return startingProperties.getProperties().get(propertyName, type);
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
