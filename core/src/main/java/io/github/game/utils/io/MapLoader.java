package io.github.game.utils.io;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import io.github.game.Game;

public final class MapLoader {

    public static MapLayer getLayer(String layerName) {
        return Game.MAP.getLayers().get(layerName);
    }

    public static MapLayers getPropertyLayers(String propertyName, Object valueToMatch) {
        MapLayers matchingLayers = new MapLayers();

        for (MapLayer layer : Game.MAP.getLayers()) {
            if (layer.getProperties().containsKey(propertyName)) {
                Object layerValue = layer.getProperties().get(propertyName);
                if (layerValue != null && layerValue.equals(valueToMatch)) {
                    matchingLayers.add(layer);
                }
            }
        }
        return matchingLayers;
    }

    public static Array<RectangleMapObject> getLayerRectangles(String layerName) {
        Array<RectangleMapObject> rectangles = new Array<>();
        MapLayer layer = getLayer(layerName);

        if (layer != null) {
            rectangles.addAll(layer.getObjects().getByType(RectangleMapObject.class));
        }
        return rectangles;
    }

    public static RectangleMapObject getLayerRectangle(String id, String layerName) {
        for (RectangleMapObject rectangle : getLayerRectangles(layerName)) {
            if (rectangle.getName().equals(id)) {
                return rectangle;
            }
        }
        return null;
    }
}
