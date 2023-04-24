package ch.progradler.rat_um_rad.shared.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Part of the test {@link StreamUtilsTest#readAndSendWorkTogether()}.
 * <p>
 * Sends two strings {@link StreamUtilsTest#SENT1} and {@link StreamUtilsTest#SENT2} to {@link StreamUtilsTestReceiver}.
 * <p>
 * For the five switch cases, see javadoc of {@link StreamUtilsTest}.
 */
public class StreamUtilsTestSender implements Runnable {
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(StreamUtilsTest.PORT);
            Socket socketInSender = serverSocket.accept();
            OutputStream outputStream = socketInSender.getOutputStream();
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socketInSender.getOutputStream(), StandardCharsets.UTF_8));
            switch (StreamUtilsTest.TEST_NUMBER) {
                case TEST_1 -> {
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT1, printWriter);
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT2, printWriter);
                    StreamUtilsTest.communicate = true;
                }
                case TEST_2 -> {
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT1, printWriter);
                    StreamUtilsTest.communicate = true;
                    while (StreamUtilsTest.communicate) {
                        Thread.sleep(200);
                    }
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT2, printWriter);
                    StreamUtilsTest.communicate = true;
                }
                case TEST_3 -> {
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT1, printWriter);
                    StreamUtilsTest.communicate = true;
                    Thread.sleep(400);
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT2, printWriter);
                }
                case TEST_4 -> {
                    Thread.sleep(400);
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT1, printWriter);
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT2, printWriter);
                    StreamUtilsTest.communicate = true;
                }
                case TEST_5 -> {
                    Thread.sleep(400);
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT1, printWriter);
                    StreamUtilsTest.communicate = true;
                    Thread.sleep(400);
                    StreamUtils.writeStringToStream(StreamUtilsTest.SENT2, printWriter);
                }
            }
            while (StreamUtilsTest.ongoing) {
                Thread.sleep(1000);
            }
            serverSocket.close();
            socketInSender.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}