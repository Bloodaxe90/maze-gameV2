package io.github.game.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import io.github.game.Game;
import io.github.game.entity.Entity;
import io.github.game.utils.io.MapLoader;

/**
 * NEW CLASS: (explanation for why in RenderSystem)
 * This class also takes on check for collision between entities and the environment found
 * in the CollisionSystem class in the forked code
 *
 * Manages the game's environment, including rendering the map and handling collisions with it
 */
public class EnvironmentSystem {

    private final OrthogonalTiledMapRenderer environmentRenderer;
    private final Array<RectangleMapObject> collidables; // Holds all collision objects from the map

    // We store the layer indices so we can render them in the correct order (background vs foreground)
    private final int[] backgroundLayers;
    private final int[] foregroundLayers;

    /**
     * Constructor for the EnvironmentSystem
     */
    public EnvironmentSystem() {
        // This is the LibGDX class that knows how to draw Tiled maps
        this.environmentRenderer = new OrthogonalTiledMapRenderer(Game.MAP, 1f);

        // Load all the rectangle objects from the "Collision" layer in Tiled
        this.collidables = MapLoader.getLayerRectangles("Collision");

        MapLayers mapLayers = Game.MAP.getLayers();
        IntArray backgroundIndices = new IntArray();
        IntArray foregroundIndices = new IntArray();

        // Loop through all the layers in the map to sort them into background and foreground
        for (int i = 0; i < mapLayers.getCount(); i++) {
            MapLayer layer = mapLayers.get(i);
            // We only care about tile layers for rendering
            if (layer instanceof TiledMapTileLayer) {
                // Check for a custom property named "background" in the Tiled editor
                if (layer.getProperties().get("background", Boolean.class)) {
                    backgroundIndices.add(i);
                } else {
                    foregroundIndices.add(i);
                }
            }
        }

        // Convert the dynamic arrays to simple int arrays for the renderer
        this.backgroundLayers = backgroundIndices.toArray();
        this.foregroundLayers = foregroundIndices.toArray();
    }

    /**
     * Renders all the background layers of the map
     * @param camera The main game camera
     */
    public void renderBackground(OrthographicCamera camera) {
        environmentRenderer.setView(camera);
        if (backgroundLayers.length > 0) {
            environmentRenderer.render(backgroundLayers);
        }
    }

    /**
     * Renders all the foreground layers of the map
     * @param camera The main game camera
     */
    public void renderForeground(OrthographicCamera camera) {
        environmentRenderer.setView(camera);
        if (foregroundLayers.length > 0) {
            // These are drawn after the player to create an illusion of depth
            environmentRenderer.render(foregroundLayers);
        }
    }

    /**
     * Checks if an entity is colliding with any of the map's collision objects
     * @param entity The entity to check for collisions
     * @return True if a collision is detected
     */
    public boolean checkCollision(Entity entity) {
        if (entity.isCollidable()) {
            // Loop through every rectangle in our "Collision" layer
            for (RectangleMapObject rectangle : collidables) {
                if (rectangle != null) {
                    Rectangle mapRect = rectangle.getRectangle();
                    // Use LibGDX's Intersector to see if the hitboxes overlap
                    if (Intersector.overlaps(entity.getHitbox(), mapRect)) {
                        return true; // Collision found
                    }
                }
            }
        }
        return false; // No collision
    }

    /** @return The size of a single tile from the map properties */
    public int getTileSize() {
        return Game.MAP.getProperties().get("tilewidth", Integer.class);
    }

    /**
     * Cleans up the map
     */
    public void dispose() {
        environmentRenderer.dispose();
    }
}
