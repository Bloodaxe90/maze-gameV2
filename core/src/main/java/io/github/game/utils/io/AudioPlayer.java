package io.github.game.utils.io;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public final class AudioPlayer {

    private static final HashMap<String, Sound> sounds = new HashMap<>();
    private static final HashMap<String, Music> tracks = new HashMap<>();

    public static Music currentMusic;
    private static boolean musicEnabled = true;
    public final static String MUSIC_PATH = "music/";
    public final static String SFX_PATH = "sfx/";



    private static void addSound(String key) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(SFX_PATH + key + ".mp3"));
        sounds.put(key, sound);
    }


    private static void addTrack(String key) {
        Music track = Gdx.audio.newMusic(Gdx.files.internal(MUSIC_PATH + key + ".mp3"));
        track.setLooping(true);
        tracks.put(key, track);
    }


    public static void playTrack(String key, float volume) {
                if (!tracks.containsKey(key)) {
            addTrack(key);
        }
        if (musicEnabled) {
                        if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }
                        currentMusic = tracks.get(key);
            currentMusic.setVolume(volume);
            currentMusic.play();
        }
    }



    public static void playSound(String key, float volume, float pitch) {
               if (!sounds.containsKey(key)) {
           addSound(key);
       }
              Sound sound = sounds.get(key);
       sound.play(volume, pitch, 1.0f);
   }


   public static void playSound(String key, float volume) {
        playSound(key, volume, 1f);
    }


    public static void setMusicEnabled(boolean status) {
        musicEnabled = status;
        if (!status) {
            currentMusic.pause();
        }
        else {
            currentMusic.play();
        }
    }
}
