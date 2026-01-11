package io.github.game.utils.io;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * NEW CLASS: (Adds additional functionality not seen in original game)
 *
 * A utility class to handle loading and playing all sounds and music
 */
public final class AudioPlayer {

    // These hashmaps store our loaded sounds and music so we don't have to load them every time
    private static final HashMap<String, Sound> sounds = new HashMap<>();
    private static final HashMap<String, Music> tracks = new HashMap<>();

    public static Music currentMusic;
    private static boolean musicEnabled = true;
    public final static String MUSIC_PATH = "music/";
    public final static String SFX_PATH = "sfx/";


    /**
     * Loads a sound effect from a file into memory
     * @param key The filename of the sound (without the extension)
     */
    private static void addSound(String key) {
        // 'Sound' is for short clips like gunshots or footsteps
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(SFX_PATH + key + ".mp3"));
        sounds.put(key, sound);
    }


    /**
     * Loads a music track from a file into memory
     * @param key The filename of the music (without the extension)
     */
    private static void addTrack(String key) {
        // 'Music' is for longer audio like background music
        Music track = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_PATH + key + ".mp3"));
        track.setLooping(true); // Make the background music loop forever
        tracks.put(key, track);
    }


    /**
     * Plays a music track
     * @param key The name of the track to play
     * @param volume How loud to play the track (0 to 1)
     */
    public static void playTrack(String key, float volume) {
        // Only load the track if we haven't loaded it before
        if (!tracks.containsKey(key)) {
            addTrack(key);
        }

        if (musicEnabled) {
            // Stop any music that is currently playing before starting a new one
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }

            // Get the track and play it
            currentMusic = tracks.get(key);
            currentMusic.setVolume(volume);
            currentMusic.play();
        }
    }


    /**
     * Plays a short sound effect with a specific pitch
     * @param key The name of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound (1 is normal >1 is higher <1 is lower)
     */
    public static void playSound(String key, float volume, float pitch) {
        // Load the sound if it's the first time we're using it
        if (!sounds.containsKey(key)) {
            addSound(key);
        }

        // Get the sound and play it
        Sound sound = sounds.get(key);
        sound.play(volume, pitch, 1.0f); // The last parameter is 'pan' (-1 left, 0 center, 1 right)
    }


    /**
     * A simpler version of playSound that uses a default pitch of 1
     * @param key The name of the sound
     * @param volume The volume of the sound
     */
    public static void playSound(String key, float volume) {
        playSound(key, volume, 1f);
    }


    /**
     * Toggles the music on or off
     * @param status True to enable music, false to disable it
     */
    public static void setMusicEnabled(boolean status) {
        musicEnabled = status;

        // If music is disabled, pause the current track
        if (!status) {
            if (currentMusic != null) currentMusic.pause();
        }
        // If music is enabled, play the current track
        else {
            if (currentMusic != null) currentMusic.play();
        }
    }
}
