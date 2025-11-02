package io.github.eng1group9.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Dean extends AnimatedEntity {
    private float speed;

    public Dean(Vector2 startPos) {
        super(new Texture("Characters/deanAnimations.png"), new int[] {4, 4,4,4} , 32, 32);
        setPosition(startPos);
        setScale(2);
    }

}
