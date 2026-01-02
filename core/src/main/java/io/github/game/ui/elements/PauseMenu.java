package io.github.game.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;
import io.github.game.utils.io.DialogueLoader;


/**
 * A UI element that shows text when the game is paused
 */
public class PauseMenu extends Element {

    private Label pauseText;

    /**
     * Constructor for the PauseMenu
     */
    public PauseMenu(String id, String hostLayer, FitViewport uiViewport, Skin skin) {
        super(id, hostLayer, uiViewport, skin);

        // Center the content of this table
        this.center();

        // Create the label with some default text loaded from our dialogue file
        pauseText = new Label("PAUSED\n\n" + DialogueLoader.getDialogue("tutorial"), skin);
        pauseText.setAlignment(Align.center);

        // Add the label to this table
        this.add(pauseText).center();
    }


    /**
     * Changes the text displayed on the pause menu
     * @param text The new text to show
     */
    public void setText(String text) {
        pauseText.setText(text);
    }


    /**
     * @return The current text being displayed on the pause menu
     */
    public String getText() {
        return pauseText.getText().toString();
    }
}
