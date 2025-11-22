package io.github.game.utils.io;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import io.github.game.Game;

public final class MapLoader {

    public static MapLayer getLayer(String layerName) {
        return Game.MAP.getLayers().get(layerName);
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
