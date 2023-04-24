package ch.progradler.rat_um_rad.shared.util;


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Helper to:
 * transfer data in {@link InputStream} to String
 * and send String into {@link OutputStream}.
 */
public class StreamUtils {

    //public static final String DELIMITER = "#####";

    public static String readStringsFromStream(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    public static void writeStringToStream(String sendStr, PrintWriter printWriter) {
        printWriter.println(sendStr);
        printWriter.flush();
    }
}
