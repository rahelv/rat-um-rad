package ch.progradler.rat_um_rad.shared.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Part of the test {@link StreamUtilsTest#readAndSendWorkTogether()}.
 * <p>
 * Receives two strings from {@link StreamUtilsTestSender} and stores them in
 * {@link StreamUtilsTest#RECEIVED1} and {@link StreamUtilsTest#RECEIVED2}.
 * <p>
 * For the five switch cases, see javadoc of {@link StreamUtilsTest}.
 */
public class StreamUtilsTestReceiver implements Runnable {
    @Override
    public void run() {
        try {
            Socket socketInReceiver = new Socket("localhost", StreamUtilsTest.PORT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketInReceiver.getInputStream(), StandardCharsets.UTF_8));

            switch (StreamUtilsTest.TEST_NUMBER) {
                case TEST_1 -> {
                    while (!StreamUtilsTest.communicate) {
                        Thread.sleep(400);
                    }
                    StreamUtilsTest.RECEIVED1 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived1 = true;
                    StreamUtilsTest.RECEIVED2 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived2 = true;
                }
                case TEST_2 -> {
                    while (!StreamUtilsTest.communicate) {
                        Thread.sleep(400);
                    }
                    StreamUtilsTest.RECEIVED1 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived1 = true;
                    StreamUtilsTest.communicate = false;
                    while (!StreamUtilsTest.communicate) {
                        Thread.sleep(400);
                    }
                    StreamUtilsTest.RECEIVED2 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived2 = true;
                }
                case TEST_3 -> {
                    while (!StreamUtilsTest.communicate) {
                        Thread.sleep(400);
                    }
                    StreamUtilsTest.RECEIVED1 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived1 = true;
                    StreamUtilsTest.RECEIVED2 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived2 = true;
                }
                case TEST_4 -> {
                    StreamUtilsTest.RECEIVED1 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived1 = true;
                    while (!StreamUtilsTest.communicate) {
                        Thread.sleep(400);
                    }
                    StreamUtilsTest.RECEIVED2 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived2 = true;
                }
                case TEST_5 -> {
                    StreamUtilsTest.RECEIVED1 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived1 = true;
                    while (!StreamUtilsTest.communicate) {
                        Thread.sleep(400);
                    }
                    StreamUtilsTest.RECEIVED2 = StreamUtils.readStringsFromStream(bufferedReader);
                    StreamUtilsTest.isReceived2 = true;
                }
            }
            while (StreamUtilsTest.ongoing) {
                Thread.sleep(1000);
            }
            socketInReceiver.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}