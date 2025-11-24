package io.github.game.utils.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public final class AnimationLoader {


    public static Animation<TextureRegion> getAnimation(String name, float duration, TextureAtlas atlas, Animation.PlayMode playMode) {

        Array<TextureRegion> frames = new Array<>();
        int frameIndex = 0;

        while (true) {
            TextureRegion currentFrame = atlas.findRegion(name + (frameIndex + 1));

            if (currentFrame == null) {
                break;
            }

            frames.add(currentFrame);
            frameIndex++;
        }
        if (frames.isEmpty()) {
            return null;
        }
        return new Animation<>(duration, frames, playMode);
    }
}
