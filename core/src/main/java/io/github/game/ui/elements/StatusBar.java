package io.github.game.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import io.github.game.ui.Element;


public class StatusBar extends Element {

    private Label status;
    private int eventsCompleted = 0;
    public static final int MAX_EVENTS = 6;
    private float timeRemaining = 300f;


    public StatusBar(String id, String hostLayer, Skin skin) {
        super(id, hostLayer, skin);
        this.top().right();
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

        updateStatusText();     }


    private void updateStatusText() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        String formattedTime = String.format("%d:%02d", minutes, seconds);

        status.setText("Events: " + eventsCompleted + "/" + MAX_EVENTS + "\nTime: " + formattedTime);
    }


    public void incrementEventCounter() {
        if (eventsCompleted < MAX_EVENTS) {
            eventsCompleted++;
        }
    }


    public String getStatusText() {
        return status.getText().toString();
    }


    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
}
