package io.github.game.entity;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.game.Game;
import io.github.game.utils.io.TriggerLoader;
import io.github.game.utils.triggers.Trigger;
import io.github.game.utils.io.AnimationLoader;
import io.github.game.utils.io.MapLoader;


public abstract class Entity {

    protected String id;
    private final RectangleMapObject startingProperties;
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
    protected TextureAtlas spriteAtlas;

    public Entity(RectangleMapObject properties,
                  TextureAtlas spriteAtlas
                  ) {

        this.startingProperties = properties;
        this.spriteAtlas = spriteAtlas;

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
        this.id = startingProperties.getName();
        Gdx.app.log("ERROR", id + "1");

        createTrigger();
        createSprites();
        this.collidable = getStartingProperty("collidable", Boolean.class);
    }

    private void createTrigger() {
        String triggerInfo = getStartingProperty("trigger", String.class);
        if (triggerInfo != null) {
            if (triggerInfo.contains(",")) {
                triggerInfo = triggerInfo.replaceFirst(",", "," + id + ",");
            } else {
                triggerInfo = triggerInfo + "," + id;
            }
        }
        this.trigger = TriggerLoader.loadTrigger(triggerInfo);

    }

    private void createSprites() {
        String sprites = getStartingProperty("sprites", String.class);
        if (sprites != null) {
            boolean initialSpriteSet = false;
            for (String spriteInfo : sprites.split(",")) {
                int lastUnderscoreIndex = spriteInfo.lastIndexOf("/");
                if (lastUnderscoreIndex != -1) {
                    String name = spriteInfo.substring(0, lastUnderscoreIndex);

                    float duration = Float.parseFloat(spriteInfo.substring(lastUnderscoreIndex + 1));
                    addSprite(name, duration);
                    if (!initialSpriteSet) {
                        initialSpriteSet = true;
                        setSprite(name);
                    }
                } else {
                    Gdx.app.log("ERROR", "No duration provided for entity " + id + "s sprites");
                }
            }
        }
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

    public void addSprite(String name, float duration) {
        String key = id + "_" + name;
        Animation<TextureRegion> animation = AnimationLoader.getAnimation(
            key,
            duration,
            spriteAtlas,
            Animation.PlayMode.LOOP);
        if (animation == null) {
            key = id.replaceAll("\\d", "") + "_" + name;
            animation = AnimationLoader.getAnimation(
                key,
                duration,
                spriteAtlas,
                Animation.PlayMode.LOOP);
        }
        spriteMap.put(key, animation);
    }


    public void setSprite(String name) {
        Animation<TextureRegion> animation = spriteMap.get(id + "_" + name);

        if (animation == null) {
            animation = spriteMap.get(id.replaceAll("\\d", "") + "_" + name);
        }

        this.sprite = animation.getKeyFrame(stateTime);

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
        return MapLoader.getCustomProperty(this.startingProperties, propertyName, type);
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public void dispose() {
        spriteAtlas.dispose();
    }
}
