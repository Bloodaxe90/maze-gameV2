package io.github.game.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;


/**
 * A UI element to display game state like score, time, and objectives
 */
public class StatusBar extends Element {

    private Label status;
    private int eventsCompleted = 0;
    private int maxEvents;
    private float timeRemaining;
    private int score = 0;


    /**
     * Constructor for the StatusBar
     */
    public StatusBar(String id, String hostLayer, FitViewport uiViewport, Skin skin) {
        super(id, hostLayer, uiViewport, skin);

        // Align the content of this table to the top right
        this.top().right();

        // Get the initial values from the Tiled map properties
        this.maxEvents = getStartingProperty("maxEvents", Integer.class);
        this.timeRemaining = getStartingProperty("startTime", Float.class);

        // The label that will display our text
        status = new Label("", skin);
        updateStatusText(); // Set the initial text
        status.setAlignment(Align.topRight);

        // Add the label to the table
        this.add(status);

    }


    /**
     * Updates the time remaining and refreshes the display text
     * @param delta The time since the last frame
     */
    public void update(float delta) {
        // Countdown the timer
        if (!isTimeUp()) {
            timeRemaining -= delta;
        } else {
            timeRemaining = 0;
        }

        // Refresh the text on the label
        updateStatusText();
    }


    /**
     * Formats the game state variables into a string for the display label
     */
    private void updateStatusText() {
        // Convert the total seconds remaining into a minutes:seconds format
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        String formattedTime = String.format("%d:%02d", minutes, seconds);

        status.setText("Events: " + eventsCompleted + "/" + maxEvents + "\nTime: " + formattedTime + "\nScore: " + score);
    }


    /**
     * Increments the event counter
     */
    public void incrementEventCounter() {
        if (eventsCompleted < maxEvents) {
            eventsCompleted++;
        }
    }

    /** @return The current score */
    public int getScore() {
        return score;
    }

    /**
     * Adds to the current score
     * @param score The amount to add
     */
    public void addScore(int score) {
        this.score += score;
    }

    /** @return The full text currently displayed in the status bar */
    public String getStatusText() {
        return status.getText().toString();
    }

    /** @return The maximum number of events for the level */
    public int getMaxEvents() {
        return maxEvents;
    }

    /** @return The time remaining in seconds */
    public float getTimeRemaining() {
        return timeRemaining;
    }

    /** @return True if the timer has run out */
    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
}
