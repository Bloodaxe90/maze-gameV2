package io.github.game.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NEW CLASS: (Adds additional functionality not seen in original game)
 *
 * A UI element for displaying and managing the games high scores
 */
public class Leaderboard extends Element {
    private final String leaderboardPath = "assets/leaderboard/leaderboard.txt";

    private List<Integer> highScores;
    private final int MAX_ENTRIES = 5;
    private final Label leaderboardLabel;

    /**
     * Constructor for the Leaderboard
     */
    public Leaderboard(String id, String hostLayer, FitViewport uiViewport, Skin skin) {
        super(id, hostLayer, uiViewport, skin);
        this.highScores = new ArrayList<>();

        // This label will display the scores
        this.leaderboardLabel = new Label("", skin);
        leaderboardLabel.setFontScale(1.2f);

        leaderboardLabel.setAlignment(Align.center);
        this.add(leaderboardLabel); // Add the label to the table

        // Load any saved scores when the game starts
        load();
        update();
    }

    /**
     * Saves the current sessions score to the leaderboard, sorts the list,
     * and writes it to a file
     */
    public void save(int score) {
        highScores.add(score);

        // Sort the scores from highest to lowest
        highScores.sort(Collections.reverseOrder());

        // Trim the list if its longer than our max entries
        if (highScores.size() > MAX_ENTRIES) {
            highScores = highScores.subList(0, MAX_ENTRIES);
        }

        // Build the string to be saved to the file
        StringBuilder builder = new StringBuilder();
        for (Integer s : highScores) {
            builder.append(s).append("\n");
        }

        try {
            FileHandle file = Gdx.files.local(leaderboardPath);
            file.writeString(builder.toString(), false); // false means overwrite the file
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "Failed to save leaderboard", e);
        }
    }

    /**
     * Loads the high scores from the save file
     */
    public void load() {
        highScores.clear();
        FileHandle file = Gdx.files.local(leaderboardPath);

        try {
            String text = file.readString();
            String[] lines = text.split("\\r?\\n"); // Split by new line

            for (String line : lines) {
                // Ignore any empty lines in the file
                if (!line.trim().isEmpty()) {
                    highScores.add(Integer.parseInt(line.trim()));
                }
            }
            // Sort the list just in case the file wasn't saved correctly
            highScores.sort(Collections.reverseOrder());
        } catch (Exception e) {
            // This might happen if the file doesn't exist yet, which is fine
            Gdx.app.error("Leaderboard", "Failed to load leaderboard", e);
        }
    }

    /**
     * Updates the text of the leaderboard label to display the current scores
     */
    private void update() {
        StringBuilder text = new StringBuilder("--- Leaderboard ---\n");
        for (int i = 0; i < highScores.size(); i++) {
            text.append(i + 1)
                .append(". ")
                .append(highScores.get(i))
                .append("\n");
        }
        // Show a message if there are no scores yet
        if (highScores.isEmpty()) {
            text.append("No scores yet");
        }
        this.leaderboardLabel.setText(text.toString());
    }
}
