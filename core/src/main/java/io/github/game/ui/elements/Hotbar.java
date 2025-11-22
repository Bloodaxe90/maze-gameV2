package io.github.game.ui.elements;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import io.github.game.Game;
import io.github.game.ui.Element;


public class Hotbar extends Element {

    public static final int NUM_SLOTS = 5;
    private Array<Image> itemIcons;
    private final TextureAtlas itemAtlas;


    public Hotbar(String id, String hostName, Skin skin, TextureAtlas uiAtlas) {
        super(id, hostName, skin, uiAtlas);

        this.itemAtlas = uiAtlas;
        this.itemIcons = new Array<>(NUM_SLOTS);

        float padding = 5f;

        float iconsY = 2f;
        float slotWidth = getWidth() / padding;
        int iconSize = (int) ((getWidth() - 46) / padding);
        float firstIconX = padding;

        for (int i = 0; i < NUM_SLOTS; i++) {
            Image itemIcon = new Image();
            itemIcon.setVisible(false);

            float iconX = firstIconX + (i * slotWidth);
            itemIcon.setPosition(iconX, iconsY);
            itemIcon.setSize(iconSize, iconSize);

            itemIcons.add(itemIcon);
            this.addActor(itemIcon);
        }
    }


    public void updateInventory(Array<Item> inventory) {
        for (int i = 0; i < NUM_SLOTS; i++) {
            Image icon = itemIcons.get(i);

                        if (i < inventory.size && inventory.get(i) != null) {
                Item item = inventory.get(i);
                icon.setDrawable(new TextureRegionDrawable(itemAtlas.findRegion(item.getName())));
                icon.setVisible(true);
            }
                        else {
                icon.setVisible(false);
            }
        }
    }
}
