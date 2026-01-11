package io.github.game.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * NEW CLASS: (Adds additional functionality not seen in original game)
 *
 * A UI element for displaying and managing the games high scores
 */
public class Leaderboard extends Element {
    private final String leaderboardPath = "assets/leaderboard/leaderboard.txt";

    private List<String> highScores;

    private final String separator = "\u200B";

    private final int maxEntries = 5;
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
     * Gets the score from the leaderboard entry string
     * @param entry The string e.g. "Eric1000"
     * @return The integer score or 0 if format is invalid
     */
    private int getScoreFromEntry(String entry) {
        try {
            String[] entryInfo = entry.split(separator, 2);
            if (entryInfo.length == 2) {
                return Integer.parseInt(entryInfo[1]);
            }
        } catch (Exception e) {
            Gdx.app.log("ERROR", "Entry in leaderboard wrong format: " + e);
        }
        return 0;
    }

    /**
     * Saves the current sessions score to the leaderboard, sorts the list,
     * and writes it to a file
     */
    public void save(String name, int score) {
        String newEntry = name + separator + score;

        // Prevent identical entries
        if (highScores.contains(newEntry)) {
            return;
        }

        highScores.add(newEntry);

        // Sort the scores from highest to lowest
        highScores.sort((e1, e2) -> {
            int score1 = getScoreFromEntry(e1);
            int score2 = getScoreFromEntry(e2);
            // Integer.compare returns -1, 0, 1
            // We reverse the order (score2 vs score1) to get a descending sort
            return Integer.compare(score2, score1);
        });
        // Trim the list if its longer than our max entries
        if (highScores.size() > maxEntries) {
            highScores = highScores.subList(0, maxEntries);
        }

        // Build the string to be saved to the file
        StringBuilder builder = new StringBuilder();
        for (String s : highScores) {
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
                    highScores.add(line);
                }
            }
            // Sort the list just in case the file wasn't saved correctly
            highScores.sort((e1, e2) -> Integer.compare(getScoreFromEntry(e2), getScoreFromEntry(e1)));
        } catch (Exception e) {
            // This might happen if the file doesn't exist yet, which is fine
            Gdx.app.error("Leaderboard", "Failed to load leaderboard", e);
        }
    }

    /**
     * Updates the text of the leaderboard label to display the current scores
     */
    public void update() {
        StringBuilder text = new StringBuilder("--- Leaderboard ---\n");
        for (int i = 0; i < highScores.size(); i++) {
            String[] entryInfo = highScores.get(i).split(separator, 2);
            if (entryInfo.length == 2) {
                String name = entryInfo[0];
                String score = entryInfo[1];

                text.append(i + 1)
                    .append(". ")
                    .append(name)
                    .append("  ")
                    .append(score)
                    .append("\n");
            }
        }
        if (highScores.isEmpty()) {
            text.append("No scores yet");
        }
        this.leaderboardLabel.setText(text.toString());
    }
}
