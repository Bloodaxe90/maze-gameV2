package io.github.eng1group9.entities;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
* An Entity is the parent class for any game object with a position and collision.
*
* @param texture The texture the entity will use.
* @param startPosition Where the entity will begin in game as Vector2.
* @param width The width of the entity.
* @param height The height of the entity.
*
*
* @author Mat.
*
*/
public class Entity {

    private Texture missingTexture = new Texture("missingTexture.png");
    private Sprite sprite = new Sprite(missingTexture);
    private Rectangle hitbox = new Rectangle();
    private boolean canCollide = true;
    private float scale = 1;
    private float width;
    private float height;

    public Entity(Texture texture, Vector2 startPosition, float width, float height) {
        sprite.setTexture(texture);
        this.width = width;
        this.height = height;
        sprite.setSize(width, height);
        sprite.setPosition(startPosition.x , startPosition.y);
        hitbox.set(startPosition.x + width, startPosition.y + height, width,    16);
    }

    public Entity(Vector2 startPosition, float width, float height) {
        sprite.setSize(width, height);
        sprite.setPosition(startPosition.x, startPosition.y);
        hitbox.set(startPosition.x  + 16, startPosition.y  + 16, width, 16);
    }

    public void setPosition(Vector2 newPosition) {
        sprite.setPosition(newPosition.x, newPosition.y);
        hitbox.setPosition(newPosition.x  + 16, newPosition.y + 16);
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
        hitbox.setPosition(x, y);
    }

    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    public void setX(float x) {
        sprite.setX(x);
        hitbox.setX(x + (0.5f * width));
    }

    public void setY(float y) {
        sprite.setY(y);
        hitbox.setY(y + (0.5f * height));
    }

    /**
    * @return the entity's x co-ordinate.
    */
    public void translate(float x, float y) {
        sprite.translate(x, y);
        hitbox.setPosition(sprite.getX(), sprite.getY());
    }

    /**
    * @return the entity's x co-ordinate.
    */
    public float getX() {
        return sprite.getX();
    }

    /**
    * @return the entity's y co-ordinate.
    */
    public float getY() {
        return sprite.getY();
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    /**
    * @return the Rectangle used as the entity's hitbox.
    */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /**
    * Toggle wether other entities can detect collision with this entity.
    */
    public void toggleCollision() {
        canCollide = !canCollide;
    }

    /**
    * @return wether this entity has collision enabled.
    */
    public boolean hasCollision() {

        return canCollide;
    }

    /**
    * @return wether this entity collides with another.
    */
    public boolean isColliding(Entity other)  {
        return other.getHitbox().contains(hitbox) && canCollide && other.hasCollision();
    }

    public float distanceTo(Entity other) {
        float dx = this.getX() - other.getX();
        float dy = this.getY() - other.getY();
        return (float)Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
    * @return wether this entity collides with a rectangle.
    */
    public boolean isColliding(Rectangle rect)  {
        return rect.overlaps(hitbox) && canCollide;
    }


    /**
     * @return the Texture used by this entity.
     */
    public Texture getTexture() {
        return sprite.getTexture();
    }

    public void setTexture(Texture newTexture) {
        sprite.setTexture(newTexture);
    }

    /**
     * @return the width of this sprite (before scaling).
     */
    public float getWidth() {
        return sprite.getWidth() * scale;
    }

    public float getHeight() {
        return sprite.getWidth() * scale;
    }

    /**
     * Sets the scale used to stretch the sprite when drawn (in x and y).
     * @param newScale
    */
    public void setScale(float newScale) {
        scale = newScale;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }
}
