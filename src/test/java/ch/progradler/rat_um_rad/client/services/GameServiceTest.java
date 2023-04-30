package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.models.game.BuildRoadInfo;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
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
}