package io.github.game.utils.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * NEW CLASS: (Adds additional functionality not seen in original game)
 *
 * A utility class with a static method to load animations
 */
public final class AnimationLoader {

    /**
     * Creates an animation by finding all frames with a given name in a texture atlas
     *
     * @param name The base name for the animation frames
     * @param duration The time to display each frame
     * @param atlas The texture atlas that contains the animation frames
     * @param playMode How the animation should loop
     * @return The created Animation object, or null if no frames were found
     */
    public static Animation<TextureRegion> getAnimation(String name, float duration, TextureAtlas atlas, Animation.PlayMode playMode) {

        // This array will hold all the frames for our animation
        Array<TextureRegion> frames = new Array<>();
        int frameIndex = 0;

        // This loop keeps looking for frames like "walk1", "walk2", "walk3"
        // until it can't find the next one in the sequence
        while (true) {
            TextureRegion currentFrame = atlas.findRegion(name + (frameIndex + 1));

            // If a frame with that name doesn't exist, we've reached the end
            if (currentFrame == null) {
                break;
            }

            frames.add(currentFrame);
            frameIndex++;
        }

        // If we didn't find any frames at all, we can't create an animation
        if (frames.isEmpty()) {
            return null;
        }

        // Create and return the new animation with the frames we found
        return new Animation<>(duration, frames, playMode);
    }
}
