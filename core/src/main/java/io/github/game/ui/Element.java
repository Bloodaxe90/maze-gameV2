package io.github.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.Game;
import io.github.game.utils.io.MapLoader;

/**
 * The base class for all UI elements in the game
 * It automatically handles positioning and sizing based on data from a Tiled map
 */
public class Element extends Table {

    protected String id;
    private RectangleMapObject startingProperties; // Stores the rectangle object from Tiled

    /**
     * Main constructor for a UI element
     *
     * @param id The name of the element (must match the object name in Tiled)
     * @param hostLayer The Tiled map layer where the elements rectangle is located
     * @param uiViewport The viewport for the UI, used for scaling
     * @param skin The skin for styling UI components like labels and buttons
     * @param uiAtlas The texture atlas containing the background image for this element
     */
    public Element(String id, String hostLayer, FitViewport uiViewport, Skin skin, TextureAtlas uiAtlas) {
        super(skin);

        // If an atlas is provided, find a region with the same name as the id
        // and set it as the background for this Table
        if (uiAtlas != null) {
            TextureRegion backgroundRegion = uiAtlas.findRegion(id);
            this.setBackground(new TextureRegionDrawable(backgroundRegion));
        }

        // Get the rectangle object from the Tiled map that defines this element's position
        this.startingProperties = MapLoader.getLayerRectangle(id, hostLayer);
        try {
            assert startingProperties != null;
            Rectangle startArea = startingProperties.getRectangle();

            // It converts the Tiled world coordinates into screen coordinates
            // by calculating their percentage position on the map
            this.setBounds(
                (startArea.x / Game.WORLD_SIZE.x) * uiViewport.getWorldWidth(),
                (startArea.y / Game.WORLD_SIZE.y) * uiViewport.getWorldHeight(),
                (startArea.width / Game.WORLD_SIZE.x) * uiViewport.getWorldWidth(),
                (startArea.height / Game.WORLD_SIZE.y) * uiViewport.getWorldHeight());

        } catch (NullPointerException e) {
            // Error if the rectangle object wasn't found in the map
            Gdx.app.log("ERROR", String.valueOf(e));
        }
    }

    /**
     * A simpler constructor for elements that don't have a background image
     *
     * @param id The name of the element
     * @param hostLayer The Tiled layer name
     * @param viewport The UI viewport
     * @param skin The UI skin
     */
    public Element(String id, String hostLayer, FitViewport viewport, Skin skin) {
        // This calls the main constructor, but passes null for the texture atlas
        this(id, hostLayer,viewport, skin, null);
    }

    /**
     * A helper method to get custom properties from the Tiled object
     * @param propertyName The name of the custom property in Tiled
     * @param type The data type of the property
     * @return The value of the property
     */
    protected <T> T getStartingProperty(String propertyName, Class<T> type) {
        return MapLoader.getCustomProperty(this.startingProperties, propertyName, type);
    }
}
