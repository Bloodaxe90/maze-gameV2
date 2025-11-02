package io.github.eng1group9.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Chest extends Entity {
    public boolean opened = false;

    public Chest() {
        super(new Texture("missingTexture.png"), new Vector2(270, 532), 32, 32);
    }

    public void open() {
        opened = true;
    }
}
