package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(mockOutputPacketGateway);
    }

    @Test
    void sendUsername() throws IOException {
        String username = "John";

        userService.sendUsername(username);

        Packet expected = new Packet(Command.NEW_USER, username, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void changeUsername() throws IOException {
        String newUsername = "John";

        userService.changeUsername(newUsername);

        Packet expected = new Packet(Command.SET_USERNAME, newUsername, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void sendBroadCastMessage() throws IOException {
        String message = "Hello!";

        userService.sendBroadCastMessage(message);

        Packet expected = new Packet(Command.SEND_BROADCAST_CHAT, message, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void sendWhisperMessage() throws IOException {
        String message = "Hello!";
        String username = "John";

        userService.sendWhisperMessage(message, username);

        Packet expected = new Packet(Command.SEND_WHISPER_CHAT,
                new ChatMessage(username, message),
                ContentType.CHAT_MESSAGE);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }
}