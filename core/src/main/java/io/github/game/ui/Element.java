package io.github.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.game.utils.io.MapLoader;

public class Element extends Table {

    protected String id;
    private RectangleMapObject startingProperties;

    public Element(String id, String hostLayer, Skin skin, TextureAtlas uiAtlas) {
        super(skin);
                if (uiAtlas != null) {
            TextureRegion backgroundRegion = uiAtlas.findRegion(id);
            this.setBackground(new TextureRegionDrawable(backgroundRegion));
        }

        this.startingProperties = MapLoader.getLayerRectangle(id, hostLayer);
        try {
            assert startingProperties != null;
            Rectangle startArea = startingProperties.getRectangle();
            this.setBounds(startArea.x, startArea.y, startArea.width, startArea.height);
        } catch (NullPointerException e) {
            Gdx.app.log("ERROR", String.valueOf(e));
        }
    }

    public Element(String id, String hostLayer, Skin skin) {
        this(id, hostLayer, skin, null);
    }
}

