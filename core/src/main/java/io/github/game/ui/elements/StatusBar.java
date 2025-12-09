package io.github.game.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;


public class StatusBar extends Element {

    private Label status;
    private int eventsCompleted = 0;
    private int maxEvents;
    private float timeRemaining;
    private int score = 0;


    public StatusBar(String id, String hostLayer, FitViewport uiViewport, Skin skin) {
        super(id, hostLayer, uiViewport, skin);
        this.top().right();
        this.maxEvents = getStartingProperty("maxEvents", Integer.class);
        this.timeRemaining = getStartingProperty("startTime", Float.class);
        status = new Label("", skin);
        updateStatusText();
        status.setAlignment(Align.topRight);
        this.add(status);
    }


    public void update(float delta) {
        if (!isTimeUp()) {
            timeRemaining -= delta;
        } else {
            timeRemaining = 0;
        }

        updateStatusText();
    }


    private void updateStatusText() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        String formattedTime = String.format("%d:%02d", minutes, seconds);

        status.setText("Events: " + eventsCompleted + "/" + maxEvents + "\nTime: " + formattedTime + "\nScore: " + score);
    }


    public void incrementEventCounter() {
        if (eventsCompleted < maxEvents) {
            eventsCompleted++;
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public String getStatusText() {
        return status.getText().toString();
    }

    public int getMaxEvents() {
        return maxEvents;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
}
