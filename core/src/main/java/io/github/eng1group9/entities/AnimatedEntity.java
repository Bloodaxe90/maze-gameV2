package io.github.eng1group9.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * An Entity which is animated.
 * Animations are stored in an ArrayList created automatially from a given spriteSheet.
 * Each animation should be on a seperate row in the spriteSheet.
 *
 * @param spriteSheetTexture The texture contaning the frames laid out in a grid, which are then used to create the aniamtions.
 * @param frameNumbers An array of integers which state how many frames are in each animation, values should match the number of sprites on each row of the spriteSheet.
 * @param tileWidth How wide each tile in the SpriteSheet is in pixels.
 * @param tileHeight How high each tile in the SpriteSheet is in pixels.
 *
 * @author Mat.
 */
public class AnimatedEntity extends Entity{

    private TextureRegion[][] spriteSheet;
    private List<Animation<TextureRegion>> animations = new ArrayList<Animation<TextureRegion>>();
    private int index = 0;

    private int tileWidth;
    private int tileHeight;
    private float frameInterval = 0.25f;
    private int currentAnimation = 0;
    private float animationPlayTime = 0f;

    public AnimatedEntity(Texture spriteSheetTexture, int[] frameNumbers, int tileWidth, int tileHeight) {
        super(new Vector2(0,0), tileWidth, tileHeight);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        TextureRegion[][] tmp = TextureRegion.split(spriteSheetTexture, tileWidth, tileHeight);
        for (int i = 0; i < frameNumbers.length; i++) {
            Animation<TextureRegion> nextAnimation = new Animation<>(frameInterval, tmp[i]);
            animations.add(nextAnimation);
        }
    }

    /**
     * Use this to change the animation being played.
     * Will have no effect if the animation is already playing.
     *
     * @param index The index of the desired animation in the list.
     */
    public void playAnimation(int index) {
        if (currentAnimation != index) { // checks if the animation is changing
            currentAnimation = index;
        }
    }

    public TextureRegion getCurrentFrame() {
        return animations.get(currentAnimation).getKeyFrame(animationPlayTime, true);
    }


    @Override
    public void draw(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        animationPlayTime += delta;
        TextureRegion currentFrame = getCurrentFrame();
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }


}
