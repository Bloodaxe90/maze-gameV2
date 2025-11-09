package io.github.eng1group9.systems;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

/**
 * System used to display temporary messages to the player. 
 */
public class ToastSystem {

    /**
     * A toast is holds the data for one of the messages being displayed.
     * @param text - The text which should be displayed to the player. 
     * @param createdDate - The time that this toast was created. 
     * @param colour - The colour of the text used for this message. 
     */
    static class Toast {
        private String text;
        private long createdDate;
        private Color colour = new Color(1, 1, 1, 1);

        public Toast(String text, long createdDate) {
            this.text = text;
            this.createdDate = createdDate;
        }

        public Toast(String text, long createdDate, Color colour) {
            this.text = text;
            this.createdDate = createdDate;
            this.colour = colour;
        }

        /**
         * @return The text assosiated with this toast. 
         */
        public String getText() {
            return text;
        }

        /**
         * @return The colour of the text with this toast. 
         */
        public Color getColour() {
            return colour;
        }

        /**
         * Set the colour used when displaying the text. 
         * @param colour 
         */
        public void setColour(Color colour) {
            this.colour = colour;
        }

        /**
         * @return The time this toast was created. 
         */
        public long getCreatedDate() {
            return createdDate;
        }
    }

    private static List<Toast> toasts = new LinkedList<>(); // List of all active toasts.

    /**
     * Add a new toast to the systm, this will sent the user a new temporary message. 
     * The colour will be White. 
     * @param text - The text you want to display. 
     */
    public static void addToast(String text) {
        Toast toast = new Toast(text, System.currentTimeMillis());
        toasts.add(toast);
    }

    /**
     * Add a new toast to the systm, this will sent the user a new temporary message. 
     * @param text - The text you want to display.
     * @param colour - The colour of the text. 
     */
    public static void addToast(String text, Color colour) {
        Toast toast = new Toast(text, System.currentTimeMillis(), colour);
        toasts.add(toast);
    }

    /**
     * Look for toasts that have lived for more than 5s and delete them. 
     */
    public static void clearExpiredToasts() {
        Toast toastToRemove = null;

        for (Toast t : toasts) {
            if (System.currentTimeMillis() - t.getCreatedDate() > 5000) {
                toastToRemove = t;
            }
        }

        if (toastToRemove != null) {
            toasts.remove(toastToRemove);
        }
    }

    /**
     * @return A list of currently displayed toasts. 
     */
    public static List<Toast> getToasts() {
        return toasts;
    }
    
    /**
     * @return A list of colours in the same order as the toasts they are assosiated with. 
     */
    public static List<Color> getToastColourList() {
        List<Color> colourList = new LinkedList<>();
        for (Toast t : toasts) {
            colourList.add(t.getColour());
        }
        return colourList;
    }
}
