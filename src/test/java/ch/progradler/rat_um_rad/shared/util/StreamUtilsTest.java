package ch.progradler.rat_um_rad.shared.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class StreamUtilsTest {
    @Test
    public void testReadStringsFromStream() throws IOException {
        String sent1 = "hello/world![#|?!&&/$$£üäö";
        String sent2 = "/$$£üäöDifferent";
        String expected = sent1 + StreamUtils.DELIMITER + sent2;

        byte[] dataBytes = expected.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes);

        List<String> result = StreamUtils.readStringsFromStream(inputStream);

        Assertions.assertEquals(Arrays.asList(sent1, sent2), result);
    }

    @Test
    public void testSendStrToStream() {
        String testSendStr = "hello/world/again,[#|?!&&/$$£üöäü";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamUtils.writeStringToStream(testSendStr, outputStream);
        Assertions.assertEquals(testSendStr + StreamUtils.DELIMITER, outputStream.toString(StandardCharsets.UTF_8));
    }
}
