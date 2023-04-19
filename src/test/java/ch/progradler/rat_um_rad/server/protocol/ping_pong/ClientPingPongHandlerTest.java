package ch.progradler.rat_um_rad.server.protocol.ping_pong;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongHandler;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static ch.progradler.rat_um_rad.shared.protocol.ClientCommand.PONG;
import static ch.progradler.rat_um_rad.shared.protocol.ContentType.NONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ClientPingPongHandlerTest {

    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    ClientPingPongHandler clientPingPongHandler;

    @BeforeEach
    void setUp() {
        clientPingPongHandler = new ClientPingPongHandler(mockOutputPacketGateway);
    }

    @Test
    void sendsPongPacketIfPingArrivedInTimeLimit() throws IOException {
        int timeCounter = 5; // below TIME_FOR_DISCONNECT;
        clientPingPongHandler.handleTimeForDisconnectOver(1, timeCounter);

        Packet.Client pingPacket = new Packet.Client(PONG, null, NONE);
        verify(mockOutputPacketGateway).sendPacket(pingPacket);
    }

    @Test
    void doesNotSendPongPacketIfPingDidNotArriveInTimeLimit() throws IOException {
        int timeCounter = 20; // below TIME_FOR_DISCONNECT;
        clientPingPongHandler.handleTimeForDisconnectOver(1, timeCounter);

        verify(mockOutputPacketGateway, never()).sendPacket(any());
    }
}
