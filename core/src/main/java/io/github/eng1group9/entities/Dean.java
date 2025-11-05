package io.github.eng1group9.entities;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Dean extends MovingEntity {
    private int reach = 3; // size of dean hitbox in tiles (3x3)
    private int moveNum = 0;
    private char[] path;

    public Dean(Vector2 startPos, float speed, char[] path) {
        super(new Texture("Characters/deanAnimations.png"), new int[] {4, 4,4,4} , 32, 32, speed);
        setPosition(startPos);
        setScale(2);

        Rectangle reachHitbox = new Rectangle();
        reachHitbox.setCenter(startPos);
        reachHitbox.setSize(reach * 32);
        setHitbox(reachHitbox);

        this.path = path;
    }

    public void nextMove(List<Rectangle> worldCollision) {
        if (!isFrozen()) {
            switch (getNextMove()) {
                case 'U':
                    
                    break;
            
                default:
                    break;
            }
        }
    }

    

    private char getNextMove() {
        return path[moveNum];
    }


}
