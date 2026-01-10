package io.github.game.ui.elements;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.game.ui.Element;
import io.github.game.utils.io.AudioPlayer;

/**
 * NEW CLASS: (Adds additional functionality not seen in original game)
 *
 * A UI element for displaying dialogue with a typewriter effect
 */
public class DialogueBox extends Element {

    private final Label textLabel;
    private String fullText;
    private int visibleTextLength = 0;
    private float letterTime; // Time between each character appearing
    private float textTimer = 0;
    ArrayList<String> sounds;
    private boolean isFinished = false; // Is the typewriter effect finished
    private ScrollPane scrollPane;

    /**
     * Constructor for the DialogueBox
     */
    public DialogueBox(String id, String hostLayer, FitViewport uiViewport, Skin skin, TextureAtlas uiAtlas) {
        super(id, hostLayer, uiViewport, skin, uiAtlas);

        // Get the typewriter speed from the Tiled map properties
        this.letterTime = getStartingProperty("letterTime", Float.class);

        // This label will hold the text that is currently visible
        textLabel = new Label("", getSkin());
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.topLeft);
        textLabel.setFontScale(1.2f);
        // A ScrollPane is used to handle text that is too long for the box
        scrollPane = new ScrollPane(textLabel, getSkin());
        // We create a new style so we can remove the scrollpanes own background
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
        style.background = null;
        scrollPane.setStyle(style);
        scrollPane.setFadeScrollBars(false); // Keep scrollbar always visible
        scrollPane.setScrollingDisabled(true, false); // Only allow vertical scrolling

        // Add the scrollpane to this Table making it fill the whole space
        this.add(scrollPane).expand().fill().pad(30);
    }


    /**
     * Resets the dialogue box with new text
     * @param text The full dialogue message to display
     * @return True if the text without SFX included is non-empty, false otherwise
     */
    public boolean startDialogue(String text) {
        // Parse the text for any sound effects and store them
        this.fullText = addSFX(text);

        // Reset all the state variables for the typewriter effect
        this.visibleTextLength = 0;
        this.textTimer = 0f;
        this.isFinished = false;
        this.textLabel.setText("");
        scrollPane.setScrollY(0);

        return !fullText.replace('\u200B', ' ').isBlank();
    }


    /**
     * Parses special sound effect tags like {sfx_name} out of the text
     * @param text The raw text from the game
     * @return The cleaned text with SFX tags removed
     */
    public String addSFX(String text) {
        sounds = new ArrayList<>();
        // This is a regex to find anything inside curly braces {}
        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Matcher matcher = pattern.matcher(text);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // Add the sound name to our list
            sounds.add(matcher.group(1));
            // Replace the {sfx} tag with a special invisible character to act as a trigger
            matcher.appendReplacement(result, "\u200B");
        }
        matcher.appendTail(result);
        return result.toString();
    }


    /**
     * Immediately finishes the typewriter effect and shows all text
     */
    public void skip() {
        if (!isFinished) {
            visibleTextLength = fullText.length();
            textLabel.setText(fullText);
            isFinished = true;

            // Force the scrollpane to update and scroll to the bottom
            scrollPane.layout();
            scrollPane.setScrollPercentY(1f);
        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        // If the text is finished or hasn't been set, do nothing
        if (isFinished || fullText == null) return;

        textTimer += delta;
        int oldLength = visibleTextLength;

        // Keep adding letters as long as we have time built up
        while (textTimer >= letterTime && visibleTextLength < fullText.length()) {
            visibleTextLength++;
            char thisChar = fullText.charAt(visibleTextLength - 1);

            // Add extra pauses for punctuation to feel more natural
            switch (thisChar) {
                case '.':
                case ':':
                case '?':
                case '!':
                    textTimer -= letterTime;
                    textTimer -= 0.5f; // Long pause
                    break;
                case ',':
                case ';':
                    textTimer -= letterTime;
                    textTimer -= 0.2f; // Short pause
                    break;
                default:
                    break;
            }

            textTimer -= letterTime;
            // This is our invisible SFX trigger character
            if (thisChar == '\u200B') {
                // Play the first sound effect found in the .txt file
                AudioPlayer.playSound(sounds.get(0), 4f);
                sounds.remove(0);
            } else {
                // Play a generic "typing" sound for each letter, with a random pitch
                AudioPlayer.playSound("speak1", 1f, MathUtils.random(2f, 3f));
            }
        }

        // Update the label text only if it has changed
        if (oldLength != visibleTextLength) {
            textLabel.setText(fullText.substring(0, visibleTextLength));
        }

        // Check if weve reached the end of the text
        if (visibleTextLength >= fullText.length()) {
            isFinished = true;
        }

        // Make the scroll pane follow the text as its being typed
        if (!isFinished) {
            scrollPane.layout();
            scrollPane.setScrollPercentY(1.0f);
        }
    }


    /**
     * Starts a new dialogue and makes the box visible
     * @param message The text to display
     */
    public void showDialogue(String message) {
        boolean isBlank = startDialogue(message);
        setVisible(isBlank);

        // Give the scrollpane "focus" so it can be controlled by the mouse wheel
        Stage stage = this.getStage();
        if (stage != null) stage.setScrollFocus(scrollPane);

    }


    /**
     * Hides the dialogue box and removes scroll focus
     */
    public void hideDialogue() {
        setVisible(false);
        Stage stage = this.getStage();
        if (stage != null && stage.getScrollFocus() == scrollPane) {
            stage.setScrollFocus(null);
        }
    }


    /**
     * @return True if the typewriter effect has finished
     */
    public boolean isFinished() {
        return isFinished;
    }
}
