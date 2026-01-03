package io.github.game.ui.elements;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.game.ui.Element;

/**
 * A UI element that displays temporary, pop up messages at the top of the screen
 */
public class ToastBar extends Element {

    /**
     * A simple inner class to keep track of a message and how long its been alive
     */
    private static class Toast {
        Label label;
        float timeAlive;
        float maxLifeTime;

        public Toast(Label label, float maxLifeTime) {
            this.label = label;
            this.timeAlive = 0f;
            this.maxLifeTime = maxLifeTime;
        }
    }

    private final List<Toast> activeToasts;
    private float duration; // How long each toast message lasts

    /**
     * Constructor for the ToastBar
     */
    public ToastBar(String id, String hostLayer, FitViewport uiViewport, Skin skin) {
        super(id, hostLayer, uiViewport, skin);
        this.activeToasts = new LinkedList<>();
        this.duration = getStartingProperty("duration", Float.class);

        // setClip(true) tells the Table to not draw children outside of its bounds
        setClip(true);
        // Align content to the top of the table
        this.top();

    }

    /**
     * Adds a new toast message with default white text
     * @param text The message to display
     */
    public void addToast(String text) {
        addToast(text, Color.WHITE);
    }

    /**
     * Adds a new toast message with a specified color
     * @param text The message to display
     * @param color The color of the text
     */
    public void addToast(String text, Color color) {
        if (!text.isEmpty()) {
            // Create the Scene2D label for the message
            Label label = new Label(text, getSkin());
            label.setColor(color);
            label.setAlignment(Align.center);
            label.setWrap(true);

            // Create our Toast tracker object
            Toast toast = new Toast(label, duration);
            activeToasts.add(toast);

            // Add the new label to this Table, making it fill the width and adding a new row
            this.add(label).width(getWidth()).row();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // We use an iterator so we can safely remove toasts from the list while looping
        Iterator<Toast> iterator = activeToasts.iterator();

        while (iterator.hasNext()) {
            Toast toast = iterator.next();
            toast.timeAlive += delta; // Update the toasts lifetime

            // In the last 3 seconds of its life, start fading the toast out
            // We do this by changing the alpha (transparency) of its color
            if (toast.timeAlive > toast.maxLifeTime - 3) {
                toast.label.getColor().a = toast.maxLifeTime - toast.timeAlive;
            }

            // If the toast has lived its full life, remove it
            if (toast.timeAlive >= toast.maxLifeTime) {
                this.removeActor(toast.label); // Remove the label from the Table
                iterator.remove(); // Remove the toast from our tracking list
            }
        }
    }
}
