package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.progradler.rat_um_rad.shared.protocol.coder.CoderHelper.SEPARATOR;
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
        String oldName = "User A";
        String newName = "new name";
        UsernameChange chatMessage = new UsernameChange(oldName, newName);

        // execute
        String result = usernameChangeCoder.encode(chatMessage);

        // assert
        String expected = oldName + SEPARATOR + newName;
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        String messageEncoded = "user A" + SEPARATOR + "new name";

        // execute
        UsernameChange result = usernameChangeCoder.decode(messageEncoded);

        // assert
        UsernameChange expected = new UsernameChange("user A", "new name");

        assertEquals(expected, result);
    }
}
