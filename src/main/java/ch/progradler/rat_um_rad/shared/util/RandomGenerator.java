package ch.progradler.rat_um_rad.shared.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Util class providing useful methods related to randomness.
 */
public class RandomGenerator {

    private static final CharSequence CHARS_FOR_ID = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_CHAR_COUNT = 12;

    public static <T> T randomFromList(List<T> list) {
        int rnd = new Random().nextInt(list.size());
        return list.get(rnd);
    }

    public static <T> T randomFromArray(T[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static char randomCharacter(CharSequence options) {
        int count = options.length();
        Random random = new Random();
        return options.charAt(random.nextInt(count - 1));
    }

    /**
     * @return {@link String} in format abcd-12ef-gh89.
     */
    public static String generateRandomId() {
        StringBuilder result = new StringBuilder();
        int length = ID_CHAR_COUNT;
        for (int i = 0; i < length; i++) {
            result.append(randomCharacter(CHARS_FOR_ID));
        }
        for (int offset = 4; offset < length; offset += 5/*every after every 4 chars a "-" is inserted*/) {
            result.insert(offset, "-");
        }
        return result.toString();
    }

    public static void shuffle(List<?> list){
        Collections.shuffle(list);
    }
}
