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
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

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
    private FileHandle mockFile;

    /**
     * Does setup and mocking needed for testing leaderboard.
     */
    @BeforeEach
    public void setup() {
        // Mock the File System.
        Gdx.files = mock(Files.class);
        mockFile = mock(FileHandle.class);

        // When the game asks for a local file, give it our mock.
        when(Gdx.files.local(anyString())).thenReturn(mockFile);

        // When reading the file, return an empty string (simulate new game).
        when(mockFile.readString()).thenReturn("");

        Game.WORLD_SIZE = new Vector2(100, 100);

        // Setup Skin.
        testSkin = new Skin();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = mock(BitmapFont.class);
        testSkin.add("default", labelStyle);

        // Setup Viewport.
        mockViewport = mock(FitViewport.class);
        when(mockViewport.getWorldWidth()).thenReturn(800f);
        when(mockViewport.getWorldHeight()).thenReturn(600f);
    }

    /**
     * Tests initialising leaderboard.
     */
    @Test
    public void testLeaderboardInitialisationAndDimensions() {
        try (MockedStatic<MapLoader> mapLoaderMock = mockStatic(MapLoader.class)) {

            RectangleMapObject mockMapObj = mock(RectangleMapObject.class);
            Rectangle mapRect = new Rectangle(20, 20, 60, 60);
            when(mockMapObj.getRectangle()).thenReturn(mapRect);

            mapLoaderMock.when(() -> MapLoader.getLayerRectangle(anyString(), anyString()))
                .thenReturn(mockMapObj);

            Leaderboard leaderboard = new Leaderboard("leaderboardID", "uiLayer", mockViewport, testSkin);

            assertAll("Leaderboard state",
                () -> assertNotNull(leaderboard, "Leaderboard should be instantiated")
                // Should do manual test on whether hotbar size looks right.
            );
        }
    }

    /**
     * Tests that the leaderboard sorts scores correctly.
     */
    @Test
    public void testLeaderboardSorting() {
        try (MockedStatic<MapLoader> mapLoaderMock = mockStatic(MapLoader.class)) {
            // Setup generic map mock.
            RectangleMapObject mockMapObj = mock(RectangleMapObject.class);
            when(mockMapObj.getRectangle()).thenReturn(new Rectangle(0,0,10,10));
            mapLoaderMock.when(() -> MapLoader.getLayerRectangle(anyString(), anyString()))
                .thenReturn(mockMapObj);

            Leaderboard leaderboard = new Leaderboard("id", "layer", mockViewport, testSkin);

            // Add scores in random order.
            leaderboard.save("LowScore", 100);
            leaderboard.save("HighScore", 500);
            leaderboard.save("MidScore", 300);

            leaderboard.update();

            String displayedText = leaderboard.toString();

            int highIndex = displayedText.indexOf("HighScore");
            int lowIndex = displayedText.indexOf("LowScore");

            assertTrue(highIndex < lowIndex,
                "Higher scores should be displayed above lower scores.");
        }
    }

    /**
     * Tests that the leaderboard keeps only the top 5 scores.
     */
    @Test
    public void testMaxEntriesLimit() {
        try (MockedStatic<MapLoader> mapLoaderMock = mockStatic(MapLoader.class)) {
            RectangleMapObject mockMapObj = mock(RectangleMapObject.class);
            when(mockMapObj.getRectangle()).thenReturn(new Rectangle(0,0,10,10));
            mapLoaderMock.when(() -> MapLoader.getLayerRectangle(anyString(), anyString()))
                .thenReturn(mockMapObj);

            Leaderboard leaderboard = new Leaderboard("id", "layer", mockViewport, testSkin);

            // Add 6 scores (100, 200, 300... 600)
            for (int i = 1; i <= 6; i++) {
                leaderboard.save("Player" + i, i * 100);
            }
            leaderboard.update();

            String displayedText = leaderboard.toString();

            assertTrue(displayedText.contains("600"), "Top score should remain");

            assertFalse(displayedText.contains("100"),
                "Lowest score should be dropped when exceeding max entries (assuming 5)");
        }
    }

    /**
     * Tests that saving the leaderboard actually writes to the file system.
     */
    @Test
    public void testFileHandling() {
        try (MockedStatic<MapLoader> mapLoaderMock = mockStatic(MapLoader.class)) {
            RectangleMapObject mockMapObj = mock(RectangleMapObject.class);
            when(mockMapObj.getRectangle()).thenReturn(new Rectangle(0,0,10,10));
            mapLoaderMock.when(() -> MapLoader.getLayerRectangle(anyString(), anyString()))
                .thenReturn(mockMapObj);

            Leaderboard leaderboard = new Leaderboard("id", "layer", mockViewport, testSkin);

            leaderboard.save("Winner", 9999);

            verify(mockFile, atLeastOnce()).writeString(anyString(), eq(false));
        }
    }
}
