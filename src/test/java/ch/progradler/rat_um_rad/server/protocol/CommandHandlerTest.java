package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {
    @Mock
    IUserService mockUserService;

    @Mock
    IGameService mockGameService;

    @Mock
    ServerPingPongRunner serverPingPongRunner;


    private CommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        commandHandler = new CommandHandler(serverPingPongRunner, mockUserService, mockGameService);
    }

    @Test
    void handlesSendBroadCastChatPacketCorrectly() {
        String message = "Hello";
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.SEND_BROADCAST_CHAT, message, ContentType.STRING);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockUserService).handleBroadCastMessageFromUser(message, ipAddress);
    }

    @Test
    void handlesSendWhisperChatPacketCorrectly() {
        String message = "Hello";
        String toUsername = "John";
        String ipAddress = "clientA";

        Packet packet = new Packet(Command.SEND_WHISPER_CHAT,
                new ChatMessage(toUsername, message), ContentType.CHAT_MESSAGE);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockUserService).handleWhisperMessageFromUser(message, toUsername, ipAddress);
    }

    @Test
    void handlesCreateGamePacketCorrectly() {
        int requiredPlayers = 4;
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.CREATE_GAME, requiredPlayers, ContentType.INTEGER);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).createGame(ipAddress, requiredPlayers);
    }

    @Test
    void handlesWaitingGamesRequest() {
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.REQUEST_GAMES, GameStatus.WAITING_FOR_PLAYERS, ContentType.GAME_STATUS);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).getWaitingGames(ipAddress);
    }

    @Test
    void handlesStartedGamesRequest() {
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.REQUEST_GAMES, GameStatus.STARTED, ContentType.GAME_STATUS);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).getStartedGames(ipAddress);
    }

    @Test
    void handlesFinishedGamesRequest() {
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.REQUEST_GAMES, GameStatus.FINISHED, ContentType.GAME_STATUS);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).getFinishedGames(ipAddress);
    }
}