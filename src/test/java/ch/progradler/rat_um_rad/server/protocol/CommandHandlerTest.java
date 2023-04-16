package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.Command;
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
        Packet packet = new Packet(Command.SEND_BROADCAST_CHAT, message, ContentType.STRING);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockUserService).handleBroadCastMessageFromUser(message, ipAddress);
    }

    @Test
    void handlesSendGameInternalChatPacketCorrectly() {
        String message = "Hello";
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.SEND_GAME_INTERNAL_CHAT, message, ContentType.STRING);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockUserService).handleGameInternalMessageFromUser(message, ipAddress);
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
    void handlesAllConnectedPlayersRequest() {
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.REQUEST_ALL_CONNECTED_PLAYERS, null, ContentType.NONE);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockUserService).requestOnlinePlayers(ipAddress);
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

    @Test
    void requestOnJoiningGameIsHandledCorrectly() {
        String ipAddress = "clientA";
        String gameId = "gameIdA";
        Packet packet = new Packet(Command.WANT_JOIN_GAME, gameId, ContentType.STRING);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).joinGame(ipAddress, gameId);
    }

    @Test
    void requestOnSelectingDestinationCardsInPrepIsHandledCorrectly() {
        String ipAddress = "clientA";
        List<String> selectedCards = Arrays.asList("card1", "card2");
        Packet packet = new Packet(Command.SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION, selectedCards, ContentType.STRING_LIST);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).selectShortDestinationCards(ipAddress, selectedCards);
    }

    @Test
    void handlesBuildRoadPacketCorrectly() {
        String roadId = "road1";
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.BUILD_ROAD, roadId, ContentType.STRING);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).buildRoad(ipAddress, roadId);
    }

    @Test
    void handlesBuildGreyRoadPacketCorrectly() {
        String roadId = "road1";
        WheelColor color = WheelColor.WHITE;
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.BUILD_ROAD, new BuildRoadInfo(roadId, color), ContentType.BUILD_ROAD_INFO);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).buildGreyRoad(ipAddress, roadId, color);
    }
}