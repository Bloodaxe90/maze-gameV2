package io.github.game.utils.io;

import com.badlogic.gdx.Gdx;


public final class DialogueLoader {

        public final static String PATH = "dialogue/";

        private final static String BLOCK_SEPERATOR = "---";


    public static String getBlock(String filename, int optionNumber) {
        if (!filename.endsWith(".txt")) filename += ".txt";
        String text = Gdx.files.internal(PATH + filename).readString();

                String[] blocks = text.split(BLOCK_SEPERATOR);

                for (int i = 0; i < blocks.length; i++) {
            blocks[i] = blocks[i].trim();
        }

                return blocks[optionNumber];
    }


    public static String getDialogue(String filename) {
        return getBlock(filename, 0);
    }
}
