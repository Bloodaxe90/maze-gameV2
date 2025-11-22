package io.github.game.ui.elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import io.github.game.ui.Element;
import io.github.game.utils.io.AudioPlayer;


public class DialogueBox extends Element {

    private final Label textLabel;
    private String fullText;
    private int visibleTextLength = 0;
    private float letterTime;
    private float textTimer = 0;
    ArrayList<String> sounds;
    private boolean isFinished = false;
    private ScrollPane scrollPane;


    public DialogueBox(String id, String hostLayer, float letterTime, Skin skin, TextureAtlas uiAtlas) {
        super(id, hostLayer, skin, uiAtlas);

        this.letterTime = letterTime;

                textLabel = new Label("", getSkin());
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.topLeft);

                scrollPane = new ScrollPane(textLabel, getSkin());
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle(scrollPane.getStyle());
        style.background = null;         scrollPane.setStyle(style);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        this.add(scrollPane).expand().fill().pad(30);
    }


    public void startDialogue(String text) {
        this.fullText = addSFX(text);
        this.visibleTextLength = 0;
        this.textTimer = 0f;
        this.isFinished = false;
        this.textLabel.setText("");
        scrollPane.setScrollY(0);
    }


    public String addSFX(String text) {
        sounds = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Matcher matcher = pattern.matcher(text);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            sounds.add(matcher.group(1));
            matcher.appendReplacement(result, "\u200B");         }
        matcher.appendTail(result);
        return result.toString();
    }


    public void skip() {
        if (!isFinished) {
            visibleTextLength = fullText.length();
            textLabel.setText(fullText);
            isFinished = true;
            scrollPane.layout();
            scrollPane.setScrollPercentY(1f);
        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if (isFinished || fullText == null) return;

        textTimer += delta;
        int oldLength = visibleTextLength;

                while (textTimer >= letterTime && visibleTextLength < fullText.length()) {
            visibleTextLength++;
                        char thisChar = fullText.charAt(visibleTextLength - 1);
                        switch (thisChar) {
                case '.':
                case ':':
                case '?':
                case '!':
                    textTimer -= letterTime;
                    textTimer -= 0.5f;
                    break;
                case ',':
                case ';':
                    textTimer -= letterTime;
                    textTimer -= 0.2f;
                    break;
                                case '\u200B':
                    AudioPlayer.playSound(sounds.get(0), 1f);
                    sounds.remove(0);
                default:
                    break;
            }

            textTimer -= letterTime;
            AudioPlayer.playSound("speak1", 1f, MathUtils.random(2f, 3f));
        }

                if (oldLength != visibleTextLength) {
            textLabel.setText(fullText.substring(0, visibleTextLength));
        }

                if (visibleTextLength >= fullText.length()) {
            isFinished = true;
        }

                if (!isFinished) {
            scrollPane.layout();
            scrollPane.setScrollPercentY(1.0f);
        }
    }


    public void showDialogue(String message) {
        startDialogue(message);
        setVisible(true);

        Stage stage = this.getStage();
        if (stage != null) stage.setScrollFocus(scrollPane);
    }


    public void hideDialogue() {
        setVisible(false);
        Stage stage = this.getStage();
        if (stage != null && stage.getScrollFocus() == scrollPane) {
            stage.setScrollFocus(null);
        }
    }


    public boolean isFinished() {
        return isFinished;
    }
}
