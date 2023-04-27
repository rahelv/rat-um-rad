package ch.progradler.rat_um_rad.shared.protocol.pingpong;

import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongHandler;
import ch.progradler.rat_um_rad.server.protocol.socket.ConnectionPool;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.progradler.rat_um_rad.shared.protocol.ContentType.NONE;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.PING;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServerPingPongHandlerTest {

    @Mock
    ConnectionPool mockConnectionPool;

    ServerPingPongHandler serverPingPongHandler;

    @BeforeEach
    void setUp() {
        serverPingPongHandler = new ServerPingPongHandler(mockConnectionPool);
    }

    @Test
    void checkDisconnectionsSendsPingIfPongArrived() {
        String ipAddress1 = "client1";
        String ipAddress2 = "client2";

        serverPingPongHandler.setPongArrived(ipAddress1);
        serverPingPongHandler.setPongArrived(ipAddress2);

        serverPingPongHandler.checkDisconnections(2);

        verify(mockConnectionPool, never()).removeConnection(ipAddress1);
        verify(mockConnectionPool, never()).removeConnection(ipAddress2);

        Packet.Server pingPacket = new Packet.Server(PING, null, NONE);
        verify(mockConnectionPool).sendPacket(ipAddress1, pingPacket);
        verify(mockConnectionPool).sendPacket(ipAddress2, pingPacket);
    }

    @Test
    void checkDisconnectionsSetsAllPongArrivedToFalseAndRemovesConnectionIfNoPongArrived() {
        String ipAddress1 = "client1";
        String ipAddress2 = "client2";
        String ipAddress3 = "client3";

        serverPingPongHandler.setPongArrived(ipAddress1);
        serverPingPongHandler.setPongArrived(ipAddress2);
        serverPingPongHandler.setPongArrived(ipAddress3);

        serverPingPongHandler.checkDisconnections(2); // to set all pongArrivedToFalse
        verify(mockConnectionPool, never()).removeConnection(ipAddress1);
        verify(mockConnectionPool, never()).removeConnection(ipAddress2);
        verify(mockConnectionPool, never()).removeConnection(ipAddress3);

        Packet.Server pingPacket = new Packet.Server(PING, null, NONE);

        // all are removed because setPongArrived never called
        serverPingPongHandler.setPongArrived(ipAddress2);

        serverPingPongHandler.checkDisconnections(3);
        // client 1 and 3 are removed
        verify(mockConnectionPool, never()).removeConnection(ipAddress2);
        verify(mockConnectionPool, times(2)).sendPacket(ipAddress2, pingPacket);

        InOrder inOrder1 = inOrder(mockConnectionPool);
        inOrder1.verify(mockConnectionPool).sendPacket(ipAddress1, pingPacket);
        inOrder1.verify(mockConnectionPool).removeConnection(ipAddress1);

        InOrder inOrder3 = inOrder(mockConnectionPool);
        inOrder3.verify(mockConnectionPool).sendPacket(ipAddress3, pingPacket);
        inOrder3.verify(mockConnectionPool).removeConnection(ipAddress3);
    }
}
