package io.github.game.headless.ui_elements;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.Game;
import io.github.game.headless.AbstractHeadlessGdxTest;
import io.github.game.ui.elements.Leaderboard;
import io.github.game.utils.io.MapLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class LeaderboardTest extends AbstractHeadlessGdxTest {

    private FitViewport mockViewport;
    private Skin testSkin;

    @BeforeEach
    public void setup() {
        // 1. Initialize Gdx.files MOCK FIRST to prevent NullPointerException
        Gdx.files = mock(Files.class);
        FileHandle mockFile = mock(FileHandle.class);
        when(Gdx.files.local(anyString())).thenReturn(mockFile);
        when(mockFile.readString()).thenReturn("");

        Game.WORLD_SIZE = new Vector2(100, 100);

        // 2. Setup Skin with a MOCKED BitmapFont
        // This avoids the "classpath" call that crashed your previous run
        testSkin = new Skin();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = mock(BitmapFont.class);
        testSkin.add("default", labelStyle);

        mockViewport = mock(FitViewport.class);
        when(mockViewport.getWorldWidth()).thenReturn(800f);
        when(mockViewport.getWorldHeight()).thenReturn(600f);
    }

    @Test
    public void testLeaderboardDimensions() {
        try (MockedStatic<MapLoader> mapLoaderMock = mockStatic(MapLoader.class)) {

            com.badlogic.gdx.maps.objects.RectangleMapObject mockMapObj =
                mock(com.badlogic.gdx.maps.objects.RectangleMapObject.class);
            com.badlogic.gdx.math.Rectangle mapRect = new com.badlogic.gdx.math.Rectangle(20, 20, 60, 60);
            when(mockMapObj.getRectangle()).thenReturn(mapRect);

            mapLoaderMock.when(() -> MapLoader.getLayerRectangle(anyString(), anyString()))
                .thenReturn(mockMapObj);

            // Correct 4-argument constructor: (id, hostLayer, viewport, skin)
            Leaderboard leaderboard = new Leaderboard("leaderboardID", "uiLayer", mockViewport, testSkin);

            /* Projection Math Check:
               X: (20/100) * 800 = 160
               Y: (20/100) * 600 = 120
               W: (60/100) * 800 = 480
               H: (60/100) * 600 = 360
            */
            assertAll("Coordinate Math",
                () -> assertEquals(160f, leaderboard.getX(), 0.01f),
                () -> assertEquals(120f, leaderboard.getY(), 0.01f),
                () -> assertEquals(480f, leaderboard.getWidth(), 0.01f),
                () -> assertEquals(360f, leaderboard.getHeight(), 0.01f)
            );
        }
    }
}
