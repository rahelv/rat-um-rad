package ch.progradler.rat_um_rad.shared.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StreamUtilsTest {

    @Test
    public void testReadStringFromStream() throws IOException {
        String expected = "hello/world!";
        byte[] dataBytes = expected.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes);

        String result = StreamUtils.readStringFromStream(inputStream);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testSendStrToStream() {
        String testSendStr = "hello/world/again";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamUtils.writeStringToStream(testSendStr, outputStream);
        Assertions.assertEquals(testSendStr, outputStream.toString());
    }
}
