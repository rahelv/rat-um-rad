package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.progradler.rat_um_rad.shared.protocol.coder.ChatMessageCoder.SEPARATOR;
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
        String username = "User A";
        String message = "Hi!";
        ChatMessage chatMessage = new ChatMessage(username, message);

        // execute
        String result = messageCoder.encode(chatMessage);

        // assert
        String expected = "{" +
                username + SEPARATOR +
                message +
                "}";
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        String messageEncoded = "{user A" + SEPARATOR + "Hi!}";

        // execute
        ChatMessage result = messageCoder.decode(messageEncoded);

        // assert
        ChatMessage expected = new ChatMessage("user A", "Hi!");

        assertEquals(expected.getUsername(), result.getUsername());
        assertEquals(expected.getMessage(), result.getMessage());
    }
}
