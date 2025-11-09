package io.github.eng1group9.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * This is the Dean, the games negative event and antagonist. 
 * They will not collide with the walls, but will catch the player if they get too close.
 * @param startPos - Where the dean will start. 
 * @param speed - How fast the dean will move. 
 * @param path - The deans path, it will follow this on loop. 
 */
public class Dean extends MovingEntity {
    private int reach = 3; // size of dean hitbox in tiles (3x3)
    private int moveNum = 0;
    private float nextTileDistance = 32;
    private Character[] path;
    private Rectangle reachRectangle;
    final Vector2 STARTPOS;

    public Dean(Vector2 startPos, float speed, Character[] path) {
        super(new Texture("Characters/deanAnimations.png"), new int[] {4, 4,4,4} , 32, 32, speed);
        setScale(2);
        setPosition(startPos);
        setHitbox(new Rectangle());

        reachRectangle = new Rectangle();
        reachRectangle.setPosition(startPos.x -32, startPos.y -42);
        reachRectangle.setSize(reach * 32);
        setHitbox(new Rectangle());
        this.path = path;
        STARTPOS = startPos;
    }

    /**
     * Move the dean along its set path.
     * It will move in a given direction until it has moved one tile, then look at the next direction. 
     */
    public void nextMove() {
        Character direction = getNextDirection();
        float distance = move(direction);
        nextTileDistance -= distance;
        if (!isFrozen()) updateAnimation(direction);
        reachRectangle.setPosition(getX() - 32, getY() - 42);
        haveIMovedOneTile();
    }

    /**
     * Check if the Dean has move on tile yet, and if so move on to the next step in its path.
     */
    private void haveIMovedOneTile() {
        if (nextTileDistance <= 0) {
            nextTileDistance = 32;
            moveNum++;
        }
    }

    /**
     * @return The direction the dean should move in next. 
     */
    private Character getNextDirection() {
        if (moveNum >= path.length) {
            moveNum = 0;
            setPosition(STARTPOS);
        }
        return path[moveNum];
    }

    /**
     * Update the dean's animation based on its current direction. 
     * @param direction - The direction the dean is moving.
     */
    private void updateAnimation(Character direction) {
        switch (direction) {
                case 'U':
                    changeAnimation(1);
                    break;
                case 'L':
                    changeAnimation(3);
                    break;
                case 'D':
                    changeAnimation(0);
                    break;
                case 'R':
                    changeAnimation(2);
                    break;
            }
    }

    /**
     * Check if a player is within the dean's reach. 
     * Will be false if the given player is invisible. 
     * @param player - The player to check against.
     * @return True if the player is in the deans reach zone.
     */
    public boolean canReach(Player player) {
        return player.isColliding(reachRectangle) && player.isVisible();
    }

    /**
     * Make the dean go back to the start of its path (first instruction). 
     */
    public void restartPath() {
        moveNum = 0;
        nextTileDistance = 32;
    }

    /**
     * @return The dean's reach rectangle. 
     */
    public Rectangle getReachRectangle() {
        return reachRectangle;
    }


}