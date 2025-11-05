package io.github.eng1group9.entities;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * An animated entity which can move. This class handles speed and collision. 
 * @param spriteSheetTexture The texture contaning the frames laid out in a grid, which are then used to create the aniamtions.
 * @param frameNumbers An array of integers which state how many frames are in each animation, values should match the number of sprites on each row of the spriteSheet.
 * @param tileWidth How wide each tile in the SpriteSheet is in pixels.
 * @param tileHeight How high each tile in the SpriteSheet is in pixels.
 * @param speed How fast the entity will move.
 */
public class MovingEntity extends AnimatedEntity {

    private float speed = 0;

    public MovingEntity(Texture spriteSheetTexture, int[] frameNumbers, int tileWidth, int tileHeight, float speed) {
        super(spriteSheetTexture, frameNumbers, tileWidth, tileHeight);
        this.speed = speed;
    }

    public void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    public float getSpeed() {
        return speed;
    }

    /**
     * Moves the entity in a given direction provided it wont collide with anything. 
     * @param direction The direction as either 'U' 'D' 'L' or'R' 
     * @param collisionRectangles A list of rectangles which the entity cannot move into.
     */
    public void move(char direction, List<Rectangle> collisionRectangles){
        float delta = Gdx.graphics.getDeltaTime();
        float distance = delta * speed;
        float newX = getX();
        float newY = getY();
        switch (direction) {
            case 'U':
                newY += distance;
                break;
            case 'D':
                newY -= distance;
                break;
            case 'L':
                newX-= distance;
                break;
            case 'R':
                newX += distance;
                break;
        }
        if (safeToMove(newX, newY, collisionRectangles)) {
                setPosition(newX, newY);
        }
    }


    private boolean safeToMove(float x, float y, List<Rectangle> collisionRectangles) {
        Rectangle testHitbox = new Rectangle();
        testHitbox = testHitbox.set(getHitbox());
        testHitbox.setPosition(x + 16, y + 16);

        for (Rectangle rectangle : collisionRectangles) {
            if (rectangle.overlaps(testHitbox)) {
                return false;
            }
        }
        return true;
    }

}
