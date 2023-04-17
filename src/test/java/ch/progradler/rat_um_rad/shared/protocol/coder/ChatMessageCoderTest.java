package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatMessageCoderTest {

    private ChatMessageCoder messageCoder;

    @BeforeEach
    public void initMessageCoder() {
        messageCoder = new ChatMessageCoder();
    }

    @Test
    public void encodeReturnsStringWithDataInOrderAndSeparatedByCorrectSeparator() {
        // prepare
        int level = 1;
        String username = "User A";
        String message = "Hi!";
        ChatMessage chatMessage = new ChatMessage(username, message);

        // execute
        String result = messageCoder.encode(chatMessage, level);

        // assert
        String expected = CoderHelper.encodeFields(level, username, message);
        assertEquals(expected, result);
    }

    public void encodeWorksWithEmptyMessage() {
        // prepare
        String username = "User A";
        ChatMessage chatMessage = new ChatMessage(username, "");
        int level = 2;

        // execute
        String result = messageCoder.encode(chatMessage, level);

        // assert
        String expected = CoderHelper.encodeFields(level, username, "");
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        int level = 1;
        String messageEncoded = CoderHelper.encodeFields(level, "user A", "Hi!");

        // execute
        ChatMessage result = messageCoder.decode(messageEncoded, level);

        // assert
        ChatMessage expected = new ChatMessage("user A", "Hi!");

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksWithEmptyStrings() {
        // prepare
        int level = 1;
        String username = "user A";
        String messageEncoded = CoderHelper.encodeFields(level, username, "");

        // execute
        ChatMessage result = messageCoder.decode(messageEncoded, level);

        // assert
        ChatMessage expected = new ChatMessage(username, "");

        assertEquals(expected, result);
    }
}
