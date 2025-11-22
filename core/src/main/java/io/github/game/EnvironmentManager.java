package io.github.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.game.utils.io.MapLoader;


public class EnvironmentManager {

    private final OrthogonalTiledMapRenderer environmentRenderer;
    private final Array<RectangleMapObject> collidables;


    public EnvironmentManager() {
                this.environmentRenderer = new OrthogonalTiledMapRenderer(Game.MAP, 1f);
        this.collidables = MapLoader.getLayerRectangles("Collision");
    }


    public void render(OrthographicCamera camera) {
                environmentRenderer.setView(camera);

                environmentRenderer.render();
    }



    public boolean checkCollision(Rectangle hitbox) {
                for (RectangleMapObject rectangle : collidables) {
            if (rectangle != null) {
                Rectangle mapRect = rectangle.getRectangle();

                                if (Intersector.overlaps(hitbox, mapRect)) {
                    return true;
                }
            }
        }
        return false;
    }



    public int getTileSize() {
        return Game.MAP.getProperties().get("tilewidth", Integer.class);
    }


    public void dispose() {
        environmentRenderer.dispose();
    }
}
