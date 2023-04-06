package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
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
    void handlesCreateGamePacketCorrectly() {
        int requiredPlayers = 4;
        String ipAddress = "clientA";
        Packet packet = new Packet(Command.CREATE_GAME, requiredPlayers, ContentType.INTEGER);

        commandHandler.handleClientCommand(packet, ipAddress);

        verify(mockGameService).createGame(ipAddress, requiredPlayers);
    }
}