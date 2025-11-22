package io.github.game.ui.elements;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import io.github.game.ui.Element;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ToastBar extends Element {

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
    private static final float DEFAULT_DURATION = 5.0f;

    public ToastBar(String id, String hostLayer, Skin skin) {
        super(id, hostLayer, skin);
        this.activeToasts = new LinkedList<>();
        setClip(true);
        this.top();

    }

    public void addToast(String text) {
        addToast(text, Color.WHITE);
    }

    public void addToast(String text, Color color) {
        Label label = new Label(text, getSkin());
        label.setColor(color);
        label.setAlignment(Align.center);
        label.setWrap(true);

        Toast toast = new Toast(label, DEFAULT_DURATION);
        activeToasts.add(toast);

        this.add(label).width(getWidth()).row();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Iterator<Toast> iterator = activeToasts.iterator();

        while (iterator.hasNext()) {
            Toast toast = iterator.next();
            toast.timeAlive += delta;

            if (toast.timeAlive > toast.maxLifeTime - 1) {
                toast.label.getColor().a = toast.maxLifeTime - toast.timeAlive;
            }

            if (toast.timeAlive >= toast.maxLifeTime) {
                this.removeActor(toast.label);
                iterator.remove();
            }
        }
    }
}
