package ch.progradler.rat_um_rad.shared.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Helper to:
 * transfer data in {@link InputStream} to String
 * and send String into {@link OutputStream}.
 */
public class StreamUtils {
    public static String readStringFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // expects no string with bigger size
        int length = stream.read(buffer);
        result.write(buffer, 0, length);
        return result.toString(StandardCharsets.UTF_8);
    }

    public static void writeStringToStream(String sendStr, OutputStream outStream) {
        byte[] bytes = sendStr.getBytes();
        try {
            outStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
