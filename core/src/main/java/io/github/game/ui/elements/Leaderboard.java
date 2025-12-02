package io.github.game.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import io.github.game.ui.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard extends Element {
    private int score = 0;
    private final String leaderboardPath = "assets/leaderboard/leaderboard.txt";

    private List<Integer> highScores;
    private final int MAX_ENTRIES = 5;
    private final Label leaderboardLabel;

    public Leaderboard(String id, String hostLayer, Skin skin) {
        super(id, hostLayer, skin);
        this.highScores = new ArrayList<>();

        this.leaderboardLabel = new Label("", skin);
        leaderboardLabel.setAlignment(Align.center);
        this.add(leaderboardLabel);

        load();
        update();
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void save() {
        highScores.add(this.score);

        highScores.sort(Collections.reverseOrder());

        if (highScores.size() > MAX_ENTRIES) {
            highScores = highScores.subList(0, MAX_ENTRIES);
        }

        StringBuilder builder = new StringBuilder();
        for (Integer s : highScores) {
            builder.append(s).append("\n");
        }

        try {
            FileHandle file = Gdx.files.local(leaderboardPath);
            file.writeString(builder.toString(), false);
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "Failed to save leaderboard", e);
        }
    }

    public void load() {
        highScores.clear();
        FileHandle file = Gdx.files.local(leaderboardPath);

        try {
            String text = file.readString();
            String[] lines = text.split("\\r?\\n");

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    highScores.add(Integer.parseInt(line.trim()));
                }
            }
            highScores.sort(Collections.reverseOrder());
        } catch (Exception e) {
            Gdx.app.error("Leaderboard", "Failed to load leaderboard", e);
        }
    }

    private void update() {
        StringBuilder text = new StringBuilder("--- HIGH SCORES ---\n");
        for (int i = 0; i < highScores.size(); i++) {
            text.append(i + 1)
                .append(". ")
                .append(highScores.get(i))
                .append("\n");
        }
        if (highScores.isEmpty()) {
            text.append("No scores yet");
        }
        this.leaderboardLabel.setText(text.toString());
    }
}
