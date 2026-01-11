package io.github.game.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;


/**
 * NEW CLASS: (Adds additional functionality not seen in original game)
 *
 * A UI element that contains a text field for the user to input their name
 */
public class NameInput extends Element {

    private final Label textLabel;
    private final TextField nameTextBox;
    private final int maxNameLength;

    /**
     * Constructor for the NameInput element
     */
    public NameInput(String id, String hostLayer, FitViewport viewport, Skin skin) {
        super(id, hostLayer, viewport, skin);
        this.maxNameLength = getStartingProperty("length", Integer.class);

        this.nameTextBox = new TextField("", skin);
        this.nameTextBox.setTouchable(Touchable.enabled);
        this.nameTextBox.setMaxLength(maxNameLength);

        this.textLabel = new Label("Enter Username:", skin);
        textLabel.setFontScale(1.2f);

        textLabel.setAlignment(Align.center);
        this.add(textLabel).center().padBottom(5);
        this.row(); // Move to the next row
        this.add(nameTextBox).width(200);
    }

    /**
     * @return The currently saved username
     */
    public String getName() {
        String name = this.nameTextBox.getText() ;
        if (name.isBlank()) {
            name = "N/A";
        }
        for (int i = 0; i < maxNameLength - name.length(); i++) {
            name += "  ";
        }
        return name;
    }

    public TextField getNameTextBox() {
        return nameTextBox;
    }
}
