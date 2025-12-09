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

public class Element extends Table {

    protected String id;
    private RectangleMapObject startingProperties;

    public Element(String id, String hostLayer, FitViewport uiViewport, Skin skin, TextureAtlas uiAtlas) {
        super(skin);
        if (uiAtlas != null) {
            TextureRegion backgroundRegion = uiAtlas.findRegion(id);
            this.setBackground(new TextureRegionDrawable(backgroundRegion));
        }

        this.startingProperties = MapLoader.getLayerRectangle(id, hostLayer);
        try {
            assert startingProperties != null;
            Rectangle startArea = startingProperties.getRectangle();
            this.setBounds(
                (startArea.x / Game.WORLD_SIZE.x) * uiViewport.getWorldWidth(),
                (startArea.y / Game.WORLD_SIZE.y) * uiViewport.getWorldHeight(),
                (startArea.width / Game.WORLD_SIZE.x) * uiViewport.getWorldWidth(),
                (startArea.height / Game.WORLD_SIZE.y) * uiViewport.getWorldHeight());
        } catch (NullPointerException e) {
            Gdx.app.log("ERROR", String.valueOf(e));
        }
    }

    public Element(String id, String hostLayer, FitViewport viewport, Skin skin) {
        this(id, hostLayer,viewport, skin, null);
    }

    protected <T> T getStartingProperty(String propertyName, Class<T> type) {
        return MapLoader.getCustomProperty(this.startingProperties, propertyName, type);
    }
}

