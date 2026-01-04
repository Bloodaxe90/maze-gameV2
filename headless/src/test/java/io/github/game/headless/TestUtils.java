package io.github.game.headless;

import static org.mockito.Mockito.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.game.utils.io.MapLoader;
import org.mockito.MockedStatic;

public class TestUtils {

    public static void mockMapLoader(MockedStatic<MapLoader> mapMock) {
        com.badlogic.gdx.maps.objects.RectangleMapObject mockMapObj =
            mock(com.badlogic.gdx.maps.objects.RectangleMapObject.class);
        when(mockMapObj.getRectangle()).thenReturn(new com.badlogic.gdx.math.Rectangle(0,0,100,50));

        mapMock.when(() -> MapLoader.getLayerRectangle(anyString(), anyString()))
            .thenReturn(mockMapObj);
    }

    public static void mockTextureAtlas(TextureAtlas mockAtlas) {
        TextureAtlas.AtlasRegion mockRegion = mock(TextureAtlas.AtlasRegion.class);
        when(mockAtlas.findRegion("mock-item")).thenReturn(mockRegion);
    }
}
