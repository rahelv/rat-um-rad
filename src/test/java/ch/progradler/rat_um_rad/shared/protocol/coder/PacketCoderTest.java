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
        ChatMessage content = new ChatMessage("userA", "Hi!");
        when(messageCoderMock.encode(content)).thenReturn("{username:userA,message:Hi!}");

        Command command = Command.SEND_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;
        Packet packet = new Packet(command, content, contentType);

        // execute
        String result = packetCoder.encode(packet);

        // assert
        String expected = command.name() + PacketCoder.SEPARATOR +
                messageCoderMock.encode(content) + PacketCoder.SEPARATOR + contentType.name();
        assertEquals(expected, result);
    }

    @Test
    public void decodeReturnsCorrectTypeWithCorrectData() {
        // prepare
        String messageEncoded = "{username:userA,message:Hi!}";
        ChatMessage expectedMessage = new ChatMessage("userA", "Hi!");
        when(messageCoderMock.decode(messageEncoded)).thenReturn(expectedMessage);

        Command command = Command.SEND_CHAT;
        ContentType contentType = ContentType.CHAT_MESSAGE;

        String packetEncoded = command.name() + PacketCoder.SEPARATOR +
                messageEncoded + PacketCoder.SEPARATOR +
                contentType.name();

        // execute
        Packet result = packetCoder.decode(packetEncoded);

        // assert
        Packet expected = new Packet(command, expectedMessage, contentType);

        assertEquals(expected,result);
    }
}
