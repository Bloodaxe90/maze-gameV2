package io.github.game.headless.ui_elements;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.Game;
import io.github.game.headless.AbstractHeadlessGdxTest;
import io.github.game.headless.TestUtils;
import io.github.game.ui.elements.Hotbar;
import io.github.game.ui.elements.Item;
import io.github.game.utils.io.MapLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class HotbarTest extends AbstractHeadlessGdxTest {

    private FitViewport mockViewport;
    private Skin testSkin;
    private TextureAtlas mockAtlas;

    @BeforeEach
    public void setup() {
        // 1. Initialize Gdx.files first to avoid NullPointerExceptions
        Gdx.files = mock(Files.class);

        Game.WORLD_SIZE = new Vector2(100, 100);
        testSkin = new Skin();

        // 2. Setup Viewport
        mockViewport = mock(FitViewport.class);
        when(mockViewport.getWorldWidth()).thenReturn(800f);
        when(mockViewport.getWorldHeight()).thenReturn(600f);

        // 3. Setup Atlas
        // We return null for regions to bypass the "setBackground" recursion loop
        mockAtlas = mock(TextureAtlas.class);
        when(mockAtlas.findRegion(anyString())).thenReturn(null);
    }

    @Test
    public void hotbarInitializationTest() {
        try (MockedStatic<MapLoader> mapMock = mockStatic(MapLoader.class)) {
            TestUtils.mockMapLoader(mapMock);

            Hotbar hotbar = new Hotbar("hotbar", "host", mockViewport, testSkin, mockAtlas);

            assertAll("Hotbar state",
                () -> assertNotNull(hotbar, "Hotbar should be instantiated"),
                () -> assertEquals(5, Hotbar.NUM_SLOTS, "Should have 5 slots defined")
                // Should do manual test on whether hotbar size looks right.
            );
        }
    }

    @Test
    public void addItemTest() {
        try (MockedStatic<MapLoader> mapMock = mockStatic(MapLoader.class)) {
            TestUtils.mockMapLoader(mapMock);
            TestUtils.mockTextureAtlas(mockAtlas);

            Hotbar hotbar = new Hotbar("hotbar", "host", mockViewport, testSkin, mockAtlas);

            Array<Item> inventory = new Array<>();
            Item mockItem = mock(Item.class);
            when(mockItem.getName()).thenReturn("mock-item");

            inventory.add(mockItem);
            hotbar.updateInventory(inventory);

            assertTrue(hotbar.getChildren().get(0).isVisible(), "First slot should be visible");
        }
    }

    @Test
    void removeItemTest() {
        try (MockedStatic<MapLoader> mapMock = mockStatic(MapLoader.class)) {
            TestUtils.mockMapLoader(mapMock);
            TestUtils.mockTextureAtlas(mockAtlas);

            Hotbar hotbar = new Hotbar("hotbar", "host", mockViewport, testSkin, mockAtlas);

            Array<Item> inventory = new Array<>();
            Item mockItem = mock(Item.class);
            when(mockItem.getName()).thenReturn("mock-item");

            inventory.add(mockItem);
            hotbar.updateInventory(inventory);

            // Check if item added successfully.
            assertTrue(hotbar.getChildren().get(0).isVisible(), "Icon must be visible before we test removal");

            inventory.removeIndex(0);
            hotbar.updateInventory(inventory);

            assertFalse(hotbar.getChildren().get(0).isVisible(),
                "Icon should be hidden after the item is removed from the inventory list");
        }
    }

    @Test
    void edgeCaseTest() {
        try (MockedStatic<MapLoader> mapMock = mockStatic(MapLoader.class)) {
            TestUtils.mockMapLoader(mapMock);
            TestUtils.mockTextureAtlas(mockAtlas);

            Hotbar hotbar = new Hotbar("hb", "layer", mockViewport, testSkin, mockAtlas);

            Array<Item> inventory = new Array<>();

            // BEHAVIOR: Boundary protection (Limit to NUM_SLOTS)
            // Hotbar has 5 slots, we give it 10 items
            for (int i = 0; i < 10; i++) {
                Item mockItem = mock(Item.class);
                when(mockItem.getName()).thenReturn("item" + i);
                inventory.add(mockItem);
            }

            assertDoesNotThrow(() -> hotbar.updateInventory(inventory),
                "The system should only attempt to update slots that exist in the UI.");

            // Assert: Verify we only have 5 icons in the Hotbar group
            // (Assuming NUM_SLOTS = 5)
            assertEquals(5, hotbar.getChildren().size,
                "The UI should not create extra icons if the inventory is larger than the hotbar.");
        }
    }
}
