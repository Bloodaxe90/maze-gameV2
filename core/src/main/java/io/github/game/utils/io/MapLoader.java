package io.github.game.utils.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import io.github.game.Game;

/**
 * A utility class with static methods to help get data from the Tiled map
 */
public final class MapLoader {

    /**
     * Gets a map layer by its name
     * @param layerName The name of the layer in the Tiled editor
     * @return The MapLayer object
     */
    public static MapLayer getLayer(String layerName) {
        // Accesses the static MAP variable from the main Game class
        return Game.MAP.getLayers().get(layerName);
    }

    /**
     * Finds all map layers that have a specific custom property
     * @param propertyName The name of the custom property to look for
     * @param valueToMatch The value that the property must have
     * @return A collection of all matching layers
     */
    public static MapLayers getPropertyLayers(String propertyName, Object valueToMatch) {
        MapLayers matchingLayers = new MapLayers();

        // Loop through every layer in the map
        for (MapLayer layer : Game.MAP.getLayers()) {
            if (layer.getProperties().containsKey(propertyName)) {
                Object layerValue = layer.getProperties().get(propertyName);
                // Check if the properties value matches what we're looking for
                if (layerValue != null && layerValue.equals(valueToMatch)) {
                    matchingLayers.add(layer);
                }
            }
        }
        return matchingLayers;
    }

    /**
     * Gets all the rectangle objects from a specific object layer
     * @param layerName The name of the object layer in Tiled
     * @return An array of all the rectangles in that layer
     */
    public static Array<RectangleMapObject> getLayerRectangles(String layerName) {
        Array<RectangleMapObject> rectangles = new Array<>();
        MapLayer layer = getLayer(layerName);

        if (layer != null) {
            // This filters the layer to get only the rectangle objects
            rectangles.addAll(layer.getObjects().getByType(RectangleMapObject.class));
        }
        return rectangles;
    }

    /**
     * Finds a specific rectangle object within a layer by its name
     * @param id The name given to the rectangle object in the Tiled editor
     * @param layerName The name of the layer the object is in
     * @return The specific RectangleMapObject or null if not found
     */
    public static RectangleMapObject getLayerRectangle(String id, String layerName) {
        for (RectangleMapObject rectangle : getLayerRectangles(layerName)) {
            // Loop through all rectangles in the layer until we find one with a matching name
            if (rectangle.getName().equals(id)) {
                return rectangle;
            }
        }
        return null;
    }

    /**
     * A helper to get a custom property from a Tiled map object
     * @param rectangle The map object to get the property from
     * @param propertyName The name of the custom property
     * @param type The data type of the property
     * @return The value of the property
     */
    public static <T> T getCustomProperty(RectangleMapObject rectangle, String propertyName, Class<T> type) {
        if (rectangle == null) return null;
        return rectangle.getProperties().get(propertyName, type);
    }
}
