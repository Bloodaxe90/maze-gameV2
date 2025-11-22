package io.github.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray; // LibGDX utility for int arrays
import io.github.game.utils.io.MapLoader;


public class EnvironmentManager {

    private final OrthogonalTiledMapRenderer environmentRenderer;
    private final Array<RectangleMapObject> collidables;

    private final int[] backgroundLayers;
    private final int[] foregroundLayers;

    public EnvironmentManager() {
        this.environmentRenderer = new OrthogonalTiledMapRenderer(Game.MAP, 1f);
        this.collidables = MapLoader.getLayerRectangles("Collision");

        MapLayers mapLayers = Game.MAP.getLayers();
        IntArray backgroundIndices = new IntArray();
        IntArray foregroundIndices = new IntArray();

        for (int i = 0; i < mapLayers.getCount(); i++) {
            MapLayer layer = mapLayers.get(i);
            if (layer instanceof TiledMapTileLayer) {
                Gdx.app.log("", layer.getName() + " " + layer.getProperties().toString());
                if (layer.getProperties().get("background", Boolean.class)) {
                    backgroundIndices.add(i);
                } else {
                    foregroundIndices.add(i);
                }
            }
        }

        this.backgroundLayers = backgroundIndices.toArray();
        this.foregroundLayers = foregroundIndices.toArray();
    }

    public void renderBackground(OrthographicCamera camera) {
        environmentRenderer.setView(camera);
        if (backgroundLayers.length > 0) {
            environmentRenderer.render(backgroundLayers);
        }
    }

    public void renderForeground(OrthographicCamera camera) {
        environmentRenderer.setView(camera);
        if (foregroundLayers.length > 0) {
            environmentRenderer.render(foregroundLayers);
        }
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
