package io.github.game.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.Game;
import io.github.game.ui.Element;


/**
 * A UI element that displays the players inventory items
 */
public class Hotbar extends Element {

    public static final int NUM_SLOTS = 5;
    private Array<Image> itemIcons; // An array to hold the Image actors for each slot
    private final TextureAtlas itemAtlas;


    /**
     * Constructor for the Hotbar
     */
    public Hotbar(String id, String hostName, FitViewport uiViewport, Skin skin, TextureAtlas uiAtlas) {
        super(id, hostName, uiViewport, skin, uiAtlas);

        this.itemAtlas = uiAtlas;
        this.itemIcons = new Array<>(NUM_SLOTS);

        // Some variables to help calculate the positions of the slots
        float padding = 5f;
        float slotWidth = getWidth() / padding;
        int iconWidth = (int) ((getWidth() - 46) / padding);
        int iconHeight = (int) (getHeight() - (padding * 2));
        float firstIconX = padding;

        // Create an Image actor for each slot in the hotbar
        for (int i = 0; i < NUM_SLOTS; i++) {
            Image itemIcon = new Image();
            itemIcon.setVisible(false); // Start with the icon hidden

            // Calculate the position of this slot
            float iconX = firstIconX + (i * slotWidth);
            itemIcon.setPosition(iconX, padding);
            itemIcon.setSize(iconWidth, iconHeight);

            itemIcons.add(itemIcon);
            this.addActor(itemIcon); // Add the icon to this group
        }
    }


    /**
     * Updates the icons in the hotbar based on the players current inventory
     * @param inventory The players inventory array
     */
    public void updateInventory(Array<Item> inventory) {
        // Loop through each of our hotbar slots
        for (int i = 0; i < NUM_SLOTS; i++) {
            Image icon = itemIcons.get(i);

            // Check if there is an item in the corresponding inventory slot
            if (i < inventory.size && inventory.get(i) != null) {
                // If there is an item, update the icon's image and make it visible
                Item item = inventory.get(i);
                icon.setDrawable(new TextureRegionDrawable(itemAtlas.findRegion(item.getName())));
                icon.setVisible(true);
            } else {
                // If there is no item, just hide the icon
                icon.setVisible(false);
            }
        }
    }
}
