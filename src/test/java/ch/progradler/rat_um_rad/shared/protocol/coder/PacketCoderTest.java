package ch.progradler.rat_um_rad.shared.protocol.coder;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PacketCoderTest {
    @Mock
    Coder<ChatMessage> messageCoderMock;

    @Mock
    Coder<UsernameChange> usernameChangeCoder;

    private PacketCoder packetCoder;

    @BeforeEach
    public void initPacketCoder() {
        packetCoder = new PacketCoder(messageCoderMock, usernameChangeCoder);
    }

    @Test
    public void encodeReturnsStringWithDataInOrderAndSeparatedByCorrectSeparator() {
        // prepare
        int level = 2;
        ChatMessage content = new ChatMessage("userA", "Hi!");

        when(messageCoderMock.encode(content, level + 1)).thenReturn("username:userA,message:Hi!");


        Command command = Command.SEND_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;
        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level, command.name(),
                "{" + messageCoderMock.encode(content, level + 1) + "}",
                contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void encodeReturnsCorrectStringIfContentTypeIsNone() {
        int level = 0;
        Command command = Command.PONG;
        ContentType contentType = ContentType.NONE;

        Packet packet = new Packet(command, null, contentType);
        String result = packetCoder.encode(packet, level);

        String expected = CoderHelper.encodeFields(level, command.name(), "null", contentType.name());
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsPacketWithContentNullIfContentEmptyTypeIsNone() {
        int level = 0;
        Command command = Command.PING;
        ContentType contentType = ContentType.NONE;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(), "null", contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, null, contentType);

        assertEquals(expected,result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        int level = 0;

        String messageEncoded = "username:userA,message:Hi!";
        ChatMessage expectedMessage = new ChatMessage("userA", "Hi!");
        when(messageCoderMock.decode(messageEncoded, level + 1)).thenReturn(expectedMessage);

        Command command = Command.SEND_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;

        String packetEncoded = CoderHelper.encodeFields(level,
                command.name(),
                "{" + messageEncoded + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, expectedMessage, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForString() {
        // prepare
        int level = 0;

        String username = "userA";

        Command command = Command.NEW_USER;
        ContentType contentType = ContentType.STRING;

        Packet packet = new Packet(command, username, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(),
                "{" + username + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForString() {
        // prepare
        int level = 0;

        String username = "userA";

        Command command = Command.NEW_USER;
        ContentType contentType = ContentType.STRING;

        String packetEncoded = CoderHelper.encodeFields(level,
                command.name(),
                "{" + username + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, username, contentType);

        assertEquals(expected, result);
    }

    @Test
    public void encodeWorksForInt() {
        // prepare
        int level = 0;

        int content = 5;

        Command command = Command.CREATE_GAME;
        ContentType contentType = ContentType.INTEGER;


        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet, level);

        // assert
        String expected = CoderHelper.encodeFields(level,
                command.name(),
                "{" + content + "}",
                contentType.name());

        assertEquals(expected, result);
    }

    @Test
    public void decodeWorksForInt() {
        // prepare
        int level = 0;

        int content = 5;

        Command command = Command.CREATE_GAME;
        ContentType contentType = ContentType.INTEGER;

        String packetEncoded = CoderHelper.encodeFields(level, command.name(),
                "{" + content + "}",
                contentType.name());

        // execute
        Packet result = packetCoder.decode(packetEncoded, level);

        // assert
        Packet expected = new Packet(command, content, contentType);

        assertEquals(expected, result);
    }
}
