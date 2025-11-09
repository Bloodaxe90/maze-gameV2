package io.github.eng1group9.systems;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles player collision. 
 */
public class CollisionSystem {
    private List<Rectangle> worldCollision;
    private TiledMap map;

    /**
     * Take a tilemap and setup a list of collision rectangles using the "Collision" layer. 
     * @param map - The tilemap (.tmx file). 
     */
    public void init(TiledMap map) {
        this.map = map;
        MapLayer collisionLayer = map.getLayers().get("Collision");
        MapObjects collisionObjects = collisionLayer.getObjects();
        worldCollision = new LinkedList<>();
        for (MapObject mapObject : collisionObjects) {
            Rectangle nextRectangle = ((RectangleMapObject) mapObject).getRectangle();
            nextRectangle.set(nextRectangle.x * 2,nextRectangle.y * 2, nextRectangle.width * 2, nextRectangle.height * 2);
            worldCollision.add(nextRectangle);
        }
    }

    public List<Rectangle> getWorldCollision() { return worldCollision; }
    
    /**
     * Serches collision Rectangle for name, then deletes from world collision by the rectangle.
     * @param name - The name of the rectangle. 
     */
    public void removeCollisionByName(String name) {
        MapLayer collisionLayer = map.getLayers().get("Collision");
        MapObjects collisionObjects = collisionLayer.getObjects();

        for (MapObject m : collisionObjects) {

            if (m.getName() == null) {
                continue;
            }

            if (!m.getName().equals(name)) {
                continue;
            }

            Rectangle r = ((RectangleMapObject) m).getRectangle();
            worldCollision.remove(r);
        }
    }

    /**
     * Check if it is safe for an entity to move to a given location. 
     * @param x - The x co-ordinate to check.
     * @param y - The y co-ordinate to check.
     * @param currentHitbox - The hitbox of the entity you wish to check. 
     * @return True if the entity will not hit collision when it moves there. 
     */
    public boolean safeToMove(float x, float y, Rectangle currentHitbox) {
        Rectangle testHitbox = new Rectangle();
        testHitbox = testHitbox.set(currentHitbox);
        testHitbox.setPosition(x + 16, y + 16);

        for (Rectangle rectangle : worldCollision) {
            if (rectangle.overlaps(testHitbox)) {
                return false;
            }
        }
        return true;
    }
}
