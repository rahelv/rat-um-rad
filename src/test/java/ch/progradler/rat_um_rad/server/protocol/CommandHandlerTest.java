package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

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
        Packet.Client packet = new Packet.Client(ClientCommand.SEND_BROADCAST_CHAT, message, ContentType.STRING);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockUserService).handleBroadCastMessageFromUser(message, ipAddress);
    }

    @Test
    void handlesSendGameInternalChatPacketCorrectly() {
        String message = "Hello";
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.SEND_GAME_INTERNAL_CHAT, message, ContentType.STRING);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockUserService).handleGameInternalMessageFromUser(message, ipAddress);
    }

    @Test
    void handlesSendWhisperChatPacketCorrectly() {
        String message = "Hello";
        String toUsername = "John";
        String ipAddress = "clientA";

        Packet.Client packet = new Packet.Client(ClientCommand.SEND_WHISPER_CHAT,
                new ChatMessage(toUsername, message), ContentType.CHAT_MESSAGE);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockUserService).handleWhisperMessageFromUser(message, toUsername, ipAddress);
    }

    @Test
    void handlesCreateGamePacketCorrectly() {
        int requiredPlayers = 4;
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.CREATE_GAME, requiredPlayers, ContentType.INTEGER);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).createGame(ipAddress, requiredPlayers);
    }

    @Test
    void handlesAllConnectedPlayersRequest() {
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_ALL_CONNECTED_PLAYERS, null, ContentType.NONE);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockUserService).requestOnlinePlayers(ipAddress);
    }

    @Test
    void handlesWaitingGamesRequest() {
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_GAMES, GameStatus.WAITING_FOR_PLAYERS, ContentType.GAME_STATUS);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).getWaitingGames(ipAddress);
    }

    @Test
    void handlesStartedGamesRequest() {
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_GAMES, GameStatus.STARTED, ContentType.GAME_STATUS);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).getStartedGames(ipAddress);
    }

    @Test
    void handlesFinishedGamesRequest() {
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_GAMES, GameStatus.FINISHED, ContentType.GAME_STATUS);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).getFinishedGames(ipAddress);
    }

    @Test
    void handlesBuildRoadPacketCorrectly() {
        String roadId = "road1";
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.BUILD_ROAD, roadId, ContentType.STRING);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).buildRoad(ipAddress, roadId);
    }

    @Test
    void requestOnJoiningGameIsHandledCorrectly() {
        String ipAddress = "clientA";
        String gameId = "gameIdA";
        Packet.Client packet = new Packet.Client(ClientCommand.WANT_JOIN_GAME, gameId, ContentType.STRING);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).joinGame(ipAddress, gameId);
    }

    @Test
    void requestTakingWheelCardsIsHandledProperly() {
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_WHEEL_CARDS, null, ContentType.NONE);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).takeWheelCardFromDeck(ipAddress);
    }

    @Test
    void requestOnSelectingDestinationCardsInPrepIsHandledCorrectly() {
        String ipAddress = "clientA";
        List<String> selectedCards = Arrays.asList("card1", "card2");
        Packet.Client packet = new Packet.Client(ClientCommand.SHORT_DESTINATION_CARDS_SELECTED, selectedCards, ContentType.STRING_LIST);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).selectShortDestinationCards(ipAddress, selectedCards);
    }

    @Test
    void requestHighscoresIsHandledCorrectly() {
        String ipAddress = "clientA";
        Packet.Client packet = new Packet.Client(ClientCommand.REQUEST_HIGHSCORES, null, ContentType.NONE);

        commandHandler.handleCommand(packet, ipAddress);

        verify(mockGameService).requestHighscores(ipAddress);
    }
}