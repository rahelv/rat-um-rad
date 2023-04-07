package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsernameChangeCoderTest {

    private UsernameChangeCoder usernameChangeCoder;

    @BeforeEach
    public void initMessageCoder() {
        usernameChangeCoder = new UsernameChangeCoder();
    }

    @Test
    public void encodeReturnsStringWithDataInOrderAndSeparatedByCorrectSeparator() {
        // prepare
        int level = 3;
        String oldName = "User A";
        String newName = "new name";
        UsernameChange chatMessage = new UsernameChange(oldName, newName);

        // execute
        String result = usernameChangeCoder.encode(chatMessage, level);

        // assert
        String expected = CoderHelper.encodeFields(level, oldName, newName);
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        int level = 4;
        String messageEncoded = CoderHelper.encodeFields(level, "user A", "new name");

        // execute
        UsernameChange result = usernameChangeCoder.decode(messageEncoded, level);

        // assert
        UsernameChange expected = new UsernameChange("user A", "new name");

        assertEquals(expected, result);
    }
}
