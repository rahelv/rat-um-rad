package ch.progradler.rat_um_rad.shared.util;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Helper to:
 * transfer data in {@link InputStream} to String
 * and send String into {@link OutputStream}.
 */
public class StreamUtils {
    public static final String DELIMITER = "#####";

    public static List<String> readStringsFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[50000]; // expects no string with bigger size
        int length = stream.read(buffer);
        result.write(buffer, 0, length);
        String asString = result.toString(StandardCharsets.UTF_8);
        String[] split = asString.split(DELIMITER);

        List<String> strings = Arrays.asList(split);
        if (strings.get(strings.size() - 1).isEmpty()) {
            strings.remove(strings.size() - 1);
        }
        return strings;

    }

    public static void writeStringToStream(String sendStr, OutputStream outStream) {
        sendStr += DELIMITER;
        byte[] bytes = sendStr.getBytes(StandardCharsets.UTF_8);
        try {
            outStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
