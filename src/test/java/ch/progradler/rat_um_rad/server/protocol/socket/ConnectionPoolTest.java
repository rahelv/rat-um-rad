package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectionPoolTest {

    @Mock
    IConnection connection1, connection2, connection3;

    private ConnectionPool connectionPool;


    @BeforeEach
    void initConnectionPoolWithAConnection() {
        connectionPool = new ConnectionPool();
        connectionPool.addConnection("client1", connection1);
        connectionPool.addConnection("client2", connection2);
        connectionPool.addConnection("client3", connection3);
    }

    @Test
    void sendMessageSendsMessageToCorrectClient() {
        Packet.Server packet = new Packet.Server(ServerCommand.NEW_USER, "Content", ContentType.STRING);

        doNothing().when(connection1).sendPacketToClient(isA(Packet.Server.class));
        doNothing().when(connection2).sendPacketToClient(isA(Packet.Server.class));

        connectionPool.sendPacket("client2", packet);
        connectionPool.sendPacket("client1", packet);
        connectionPool.sendPacket("client2", packet);

        verify(connection1, times(1)).sendPacketToClient(packet);
        verify(connection2, times(2)).sendPacketToClient(packet);
        verify(connection3, times(0)).sendPacketToClient(packet);
    }


    @Test
    void broadcastExcludeSendsMessageToCorrectClients() {
        Packet.Server packet = new Packet.Server(ServerCommand.NEW_USER, "Content", ContentType.STRING);

        doNothing().when(connection1).sendPacketToClient(isA(Packet.Server.class));
        doNothing().when(connection3).sendPacketToClient(isA(Packet.Server.class));

        List<String> exclude = Collections.singletonList("client2");

        connectionPool.broadCastExclude(packet, exclude);

        verify(connection1, times(1)).sendPacketToClient(packet);
        verify(connection2, times(0)).sendPacketToClient(packet);
        verify(connection3, times(1)).sendPacketToClient(packet);
    }

    @Test
    void broadcastOnlySendsMessageToCorrectClients() {
        Packet.Server packet = new Packet.Server(ServerCommand.NEW_USER, "Content", ContentType.STRING);

        String client1 = "client1", client3 = "client3";
        List<String> only = Arrays.asList(client1, client3);

        connectionPool.broadCastOnly(packet, only);

        verify(connection1, times(1)).sendPacketToClient(packet);
        verify(connection2, times(0)).sendPacketToClient(packet);
        verify(connection3, times(1)).sendPacketToClient(packet);
    }

    @Test
    void removeConnectionThrowsNoErrorIfIPAddressNotPresent() {
        assertDoesNotThrow(() -> connectionPool.removeConnection("nonExistentClient"));
    }

    @Test
    void removeConnectionCallsCloseOnCorrectConnectionAndRemovesFromMap() throws IOException {
        doNothing().when(connection2).close();
        connectionPool.removeConnection("client2");
        connectionPool.removeConnection("client2");
        // should already be removed after first call -> only 1 close call
        verify(connection2, times(1)).close();
    }

    @Test
    void sendPacketThrowsNoErrorIfIPAddressNotPresent() {
        Packet.Server packet = new Packet.Server(ServerCommand.PING, null, ContentType.NONE);
        assertDoesNotThrow(() -> connectionPool.sendPacket("nonExistentClient", packet));
    }
}
