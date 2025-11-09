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

    private Texture missingTexture = new Texture("missingTexture.png"); // Used if no texture is avalible for the entity. 
    private Sprite sprite = new Sprite(missingTexture);
    private Rectangle hitbox = new Rectangle(); 
    private boolean canCollide = true; // wether the entity will collide with other entitys / rectangles.
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

    /**
     * Change the position of the entity using a Vector2D.
     * @param newPosition - A vector defining the new position. 
     */
    public void setPosition(Vector2 newPosition) {
        sprite.setPosition(newPosition.x, newPosition.y);
        hitbox.setPosition(newPosition.x  + 16, newPosition.y + 16);
    }

    /**
     * Change the position of the entity using a two float values.
     * @param x - The entity's new x co-ordinate.
     * @param y - The entity's new y co-ordinate.
     */
    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
        hitbox.setPosition(x, y);
    }

    /**
     * @return The current position of the entity as a Vector2D.
     */
    public Vector2 getPosition() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    /**
     * Set the x co-ordinate of the entity. 
     * @param x - The new x co-ordinate. 
     */
    public void setX(float x) {
        sprite.setX(x);
        hitbox.setX(x + (0.5f * width));
    }

    /**
     * Set the y co-ordinate of the entity. 
     * @param y - The new x co-ordinate. 
     */
    public void setY(float y) {
        sprite.setY(y);
        hitbox.setY(y + (0.5f * height));
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

    /**
     * Render the entity. 
     * @param batch - The SpriteBatch this entity should be rendered with. 
     */
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
     * @return the width of this entity (after scaling).
     */
    public float getWidth() {
        return sprite.getWidth() * scale;
    }

    /**
     * @return the height of this entity (after scaling).
     */
    public float getHeight() {
        return sprite.getWidth() * scale;
    }

    /**
     * Sets the scale used to stretch the entity when drawn (in x and y).
     * @param newScale
    */
    public void setScale(float newScale) {
        scale = newScale;
    }

    /**
     * Update the hitbox of an entity.  
     * @param hitbox - The entity's new hitbox. 
     */
    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }
}
