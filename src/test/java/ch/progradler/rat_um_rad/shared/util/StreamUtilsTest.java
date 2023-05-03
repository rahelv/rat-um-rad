package ch.progradler.rat_um_rad.shared.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * The fields in this class are used for the test {@link StreamUtilsTest#readAndSendWorkTogether()}.
 */
public class StreamUtilsTest {
    public final static String SENT1 = "hello/world![#|?!&&/$$£üäö";
    public final static String SENT2 = "/$$£üäöDifferent";
    public static final int PORT = 6666;
    public static String RECEIVED1;
    public static String RECEIVED2;
    public static boolean isReceived1;
    public static boolean isReceived2;
    /**
     * Since the test {@link StreamUtilsTest#readAndSendWorkTogether()} uses two Threads for sending and receiving
     * strings, both threads must be closed. To ensure that none of them is closed too early, they will check
     * whether this variable is false and if so, they will close.
     */
    public static boolean ongoing;
    /**
     * Since the order of several attempts to send a string and several attempts to read them can change,
     * for all cases it needs to be ensured that no information gets lost. So, five different
     * test scenarios have been chosen, represented in the switch statement of {@link StreamUtilsTestSender} and {@link StreamUtilsTestSender}.
     * The five different orders are the following ones:
     * 1) send - send - read - read
     * 2) send - read - send - read
     * 3) send - read - read - send
     * 4) read - send - read - send
     * 5) read - send - send - read
     * <p>
     * This variable indicates which test scenario needs to be tested.
     */
    public static StreamUtilsTestScenarioIndicator TEST_NUMBER;
    /**
     * For the threads to know exactly when their turn is to do an action (to ensure the chosen test scenario,
     * see javadoc of {@link StreamUtilsTest#TEST_NUMBER}), they turn this variable to true and false.
     */
    public static boolean communicate;

    @Test
    public void testReadStringsFromStream() throws IOException {
        String sent = "hello/world![#|?!&&/$$£üäö/$$£üäöDifferent";

        byte[] dataBytes1 = sent.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes1);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String result = StreamUtils.readStringsFromStream(bufferedReader);

        Assertions.assertEquals(sent, result);
    }

    @Test
    public void testSendStrToStream() {
        String testSendStr = "hello/world/again,[#|?!&&/$$£üöäü";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        StreamUtils.writeStringToStream(testSendStr, printWriter);
        String actualString = outputStream.toString(StandardCharsets.UTF_8);

        Assertions.assertEquals(testSendStr + System.lineSeparator(), actualString);
        System.out.println();
    }

    @Test
    public void readAndSendWorkTogether() throws IOException {
        //all five different test scenarios are tested. See javadoc of the fields.
        for (StreamUtilsTestScenarioIndicator test_number : StreamUtilsTestScenarioIndicator.values()) {
            //preparation after each of the five tests
            RECEIVED1 = "";
            RECEIVED2 = "";
            isReceived1 = false;
            isReceived2 = false;
            TEST_NUMBER = test_number;
            communicate = false;

            StreamUtilsTestSender sender = new StreamUtilsTestSender();
            Thread senderThread = new Thread(sender);
            senderThread.start();
            StreamUtilsTestReceiver receiver = new StreamUtilsTestReceiver();
            Thread receiverThread = new Thread(receiver);
            receiverThread.start();

            //waiting for threads to do their work
            while (!(isReceived1 && isReceived2)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //closing threads
            ongoing = false;

            //test
            Assertions.assertEquals(SENT1, RECEIVED1);
            Assertions.assertEquals(SENT2, RECEIVED2);
        }
    }

}
