package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.CommandHandler;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.packet.ClientPacketCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.packet.ServerPacketCoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    @Test
    void getIpOfThreadWorks() {
        int PORT = 6666;
        try {
            CommandHandler mockCommandHandler = mock(CommandHandler.class);
            ClientPacketCoder mockClientPacketCoder = mock(ClientPacketCoder.class);
            ServerPingPongRunner serverPingPongRunner = mock(ServerPingPongRunner.class);
            ServerPacketCoder mockServerPacketCoder = mock(ServerPacketCoder.class);
            ServerSocket serverSocket = new ServerSocket(PORT);

            //first connection
            Socket socketInClient1 = new Socket("localhost", PORT);
            Socket socketInServer1 = serverSocket.accept();
            String ipAddress1 = socketInServer1.getRemoteSocketAddress().toString();
            ClientInputListener clientInputListener1= new ClientInputListener(socketInClient1,
                    mockCommandHandler,
                    mockClientPacketCoder,
                    ipAddress1,
                    connectionPool,
                    serverPingPongRunner);
            ClientOutput clientOutput1 = new ClientOutput(socketInServer1, ipAddress1, mockServerPacketCoder, connectionPool);
            connectionPool.addConnection(ipAddress1, new Connection(socketInServer1, clientOutput1, clientInputListener1));
            Thread thread1 = new Thread(clientInputListener1);
            clientInputListener1.setThread(thread1);

            //second connection
            Socket socketInClient2 = new Socket("localhost", PORT);
            Socket socketInServer2 = serverSocket.accept();
            String ipAddress2 = socketInServer2.getRemoteSocketAddress().toString();
            ClientInputListener clientInputListener2 = new ClientInputListener(socketInClient2,
                    mockCommandHandler,
                    mockClientPacketCoder,
                    ipAddress2,
                    connectionPool,
                    serverPingPongRunner);
            ClientOutput clientOutput2 = new ClientOutput(socketInServer2, ipAddress2, mockServerPacketCoder, connectionPool);
            connectionPool.addConnection(ipAddress2, new Connection(socketInServer2, clientOutput2, clientInputListener2));
            Thread thread2 = new Thread(clientInputListener2);
            clientInputListener2.setThread(thread2);

            //testing
            Assertions.assertEquals(ipAddress1, connectionPool.getIpOfThread(thread1));
            Assertions.assertEquals(ipAddress2, connectionPool.getIpOfThread(thread2));
            Assertions.assertNotEquals(ipAddress1, connectionPool.getIpOfThread(thread2));
            Assertions.assertNotEquals(ipAddress2, connectionPool.getIpOfThread(thread1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}