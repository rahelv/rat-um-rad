package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(mockOutputPacketGateway);
    }

    @Test
    void createGame() throws IOException {
        int requiredPlayerCount = 5;

        gameService.createGame(requiredPlayerCount);

        Packet.Client expected = new Packet.Client(ClientCommand.CREATE_GAME, requiredPlayerCount, ContentType.INTEGER);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void buildRoad() throws IOException {
        String roadId = "road1";

        gameService.buildRoad(roadId);

        Packet.Client expected = new Packet.Client(ClientCommand.BUILD_ROAD, roadId, ContentType.STRING);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void buildRoadGrey() throws IOException {
        String roadId = "road1";
        WheelColor color = WheelColor.WHITE;

        gameService.buildGreyRoad(roadId, color);

        Packet.Client expected = new Packet.Client(ClientCommand.BUILD_ROAD, new BuildRoadInfo(roadId, color), ContentType.BUILD_ROAD_INFO);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void requestHighscores() throws IOException {
        gameService.requestHighscores();

        Packet.Client expected = new Packet.Client(ClientCommand.REQUEST_HIGHSCORES, null, ContentType.NONE);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }

    @Test
    void requestWaitingGamesWorks() throws IOException {
        gameService.requestWaitingGames();
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.REQUEST_GAMES, GameStatus.WAITING_FOR_PLAYERS, ContentType.GAME_STATUS));
    }

    @Test
    void requestStartedGamesWorks() throws IOException {
        gameService.requestStartedGames();
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.REQUEST_GAMES, GameStatus.STARTED, ContentType.GAME_STATUS));
    }

    @Test
    void requestFinishedGamesWorks() throws IOException {
        gameService.requestFinishedGames();
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.REQUEST_GAMES, GameStatus.FINISHED, ContentType.GAME_STATUS));
    }

    @Test
    void selectCardsWorks() throws IOException {
        List<String> selectedItems = mock(List.class);
        gameService.selectCards(selectedItems);
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.SHORT_DESTINATION_CARDS_SELECTED, selectedItems, ContentType.STRING_LIST));
    }

    @Test
    void requestWheelCardsWorks() throws IOException {
        gameService.requestWheelCards();
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.REQUEST_WHEEL_CARDS, null, ContentType.NONE));
    }

    @Test
    void requestShortDestinationCardsWorks() throws IOException {
        gameService.requestShortDestinationCards();
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.REQUEST_SHORT_DESTINATION_CARDS, null, ContentType.NONE));
    }

    @Test
    void joinGameWorks() throws IOException {
        String gameId = "gameId";
        gameService.joinGame(gameId);
        verify(mockOutputPacketGateway).sendPacket(new Packet.Client(ClientCommand.WANT_JOIN_GAME, gameId, ContentType.STRING));
    }
}