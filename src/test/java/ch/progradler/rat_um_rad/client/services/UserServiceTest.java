package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
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

        Packet.Client expected = new Packet.Client(ClientCommand.REGISTER_USER, username, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void changeUsername() throws IOException {
        String newUsername = "John";

        userService.changeUsername(newUsername);

        Packet.Client expected = new Packet.Client(ClientCommand.SET_USERNAME, newUsername, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void sendBroadCastMessage() throws IOException {
        String message = "Hello!";

        userService.sendBroadCastMessage(message);

        Packet.Client expected = new Packet.Client(ClientCommand.SEND_BROADCAST_CHAT, message, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void sendGameInternalMessage() throws IOException {
        String message = "Hello!";

        userService.sendGameInternalMessage(message);

        Packet.Client expected = new Packet.Client(ClientCommand.SEND_GAME_INTERNAL_CHAT, message, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void sendWhisperMessage() throws IOException {
        String message = "Hello!";
        String username = "John";

        userService.sendWhisperMessage(message, username);

        Packet.Client expected = new Packet.Client(ClientCommand.SEND_WHISPER_CHAT,
                new ChatMessage(username, message),
                ContentType.CHAT_MESSAGE);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void requestOnlinePlayers() throws IOException {
        userService.requestOnlinePlayers();
        Packet.Client expected = new Packet.Client(ClientCommand.REQUEST_ALL_CONNECTED_PLAYERS, null, ContentType.NONE);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }
}