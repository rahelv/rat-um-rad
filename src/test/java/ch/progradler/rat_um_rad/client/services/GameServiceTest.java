package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
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
class GameServiceTest {

    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @Test
    void createGame() throws IOException {
        int requiredPlayerCount = 5;

        gameService.createGame(requiredPlayerCount);

        Packet expected = new Packet(Command.CREATE_GAME, requiredPlayerCount, ContentType.INTEGER);
        verify(mockOutputPacketGateway).sendPacket(expected);
    }
}