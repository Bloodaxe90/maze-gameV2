package io.github.game.utils.io;
import com.badlogic.gdx.Gdx;

/**
 * A utility class to load dialogue text from files
 */
public final class DialogueLoader {

    public final static String PATH = "assets/dialogue/";

    // The string used in the text files to separate different blocks of dialogue
    private final static String BLOCK_SEPERATOR = "---";

    /**
     * Gets a specific block of text from a dialogue file
     *
     * @param id The filename of the dialogue
     * @param optionNumber The index of the block to get (0 for the first one, 1 for the second and so on)
     * @return The requested block of text as a string
     */
    public static String getBlock(String id, int optionNumber) {
        // Make sure the filename has the .txt extension
        if (!id.endsWith(".txt")) id += ".txt";

        String text;
        try {
            try {
                // Try to read the file with the full name
                text = Gdx.files.internal(PATH + id).readString();
            } catch (Exception e) {
                // If that fails try a generic version by removing numbers
                text = Gdx.files.internal(PATH + id.replaceAll("\\d", "")).readString();
            }
        } catch (Exception e) {
            Gdx.app.log("ERROR", "Failed to find " + id + " in " + PATH);
            return "";
        }

        // Split the file's content into different blocks using our separator
        String[] blocks = text.split(BLOCK_SEPERATOR);

        // Trim any extra whitespace from the beginning or end of each block
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = blocks[i].trim();
        }

        // Check if the requested block number is valid
        if (optionNumber > blocks.length - 1 || optionNumber < 0) {
            // If the number is too high, return an empty string to prevent a crash
            return "";
        }

        // Return the specific block of text we wanted
        return blocks[optionNumber];
    }


    /**
     * A simpler helper method to get the first block of dialogue from a file
     * @param filename The name of the dialogue file
     * @return The first block of text in the file
     */
    public static String getDialogue(String filename) {
        return getBlock(filename, 0);
    }
}
