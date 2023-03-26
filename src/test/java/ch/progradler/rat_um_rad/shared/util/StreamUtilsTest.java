package ch.progradler.rat_um_rad.shared.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StreamUtilsTest {

    @Test
    public void testReadStringFromStream() throws IOException {
        String expected = "hello/world![#|?!&&/$$£üäö";

        byte[] dataBytes = expected.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes);

        String result = StreamUtils.readStringFromStream(inputStream);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testSendStrToStream() {
        String testSendStr = "hello/world/again,[#|?!&&/$$£üöäü";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamUtils.writeStringToStream(testSendStr, outputStream);
        Assertions.assertEquals(testSendStr, outputStream.toString(StandardCharsets.UTF_8));
    }
}
