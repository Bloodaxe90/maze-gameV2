package io.github.game.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import io.github.game.ui.Element;
import io.github.game.utils.io.DialogueLoader;


public class PauseMenu extends Element {

    private Label pauseText;

    public PauseMenu(String id, String hostLayer, Skin skin) {
        super(id, hostLayer, skin);
        this.center();
        pauseText = new Label("PAUSED\n\n" + DialogueLoader.getDialogue("tutorial"), skin);
        pauseText.setAlignment(Align.center);

        this.add(pauseText).center();
    }


    public void setText(String text) {
        pauseText.setText(text);
    }


    public String getText() {
        return pauseText.getText().toString();
    }
}
