package io.github.eng1group9.entities;

import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Dean extends AnimatedEntity {
    private float speed = 10;

    public Dean(Vector2 startPos) {
        super(new Texture("Characters/deanAnimations.png"), new int[] {4, 4,4,4} , 32, 32);
        setPosition(startPos);
        setScale(2);
    }

    public void setSpeed(float newSpeed) {
        speed = newSpeed;
    }

    public float getSpeed() {
        return speed;
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
