package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.protocol.socket.ClientInputListener;
import ch.progradler.rat_um_rad.server.protocol.socket.ClientOutput;
import ch.progradler.rat_um_rad.server.protocol.socket.Connection;
import ch.progradler.rat_um_rad.server.protocol.socket.ConnectionPool;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static ch.progradler.rat_um_rad.shared.protocol.ContentType.NONE;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.PING;

/**
 * Manages client connections and starts process of listening for client connection requests.
 */
public class ClientConnectionsHandler {
    public final ConnectionPool connectionPool = new ConnectionPool();
    private final Coder<Packet<ServerCommand>> serverPacketCoder;
    private final Coder<Packet<ClientCommand>> clientPacketCoder;
    public static final Logger LOGGER = LogManager.getLogger();

    public ClientConnectionsHandler(Coder<Packet<ServerCommand>> serverPacketCoder, Coder<Packet<ClientCommand>> clientPacketCoder) {
        this.serverPacketCoder = serverPacketCoder;
        this.clientPacketCoder = clientPacketCoder;
    }

    /**
     * Starts listening for incoming client connection requests.
     */
    public void start(int port, InputPacketGateway inputPacketGateway, ServerPingPongRunner serverPingPongRunner) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            LOGGER.info("Server listening on port {}", port);

            while (true) { //keeps running
                acceptNewClient(inputPacketGateway, serverSocket, serverPingPongRunner);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create server socket.", e);
        }
    }

    private void acceptNewClient(InputPacketGateway inputPacketGateway, ServerSocket serverSocket, ServerPingPongRunner serverPingPongRunner) {
        try {
            Socket socket = serverSocket.accept();
            setupConnection(socket, inputPacketGateway, serverPingPongRunner);
        } catch (IOException e) {
            LOGGER.warn("Failed to accept client connection.", e);
        }
    }

    private void setupConnection(Socket socket, InputPacketGateway inputPacketGateway, ServerPingPongRunner serverPingPongRunner) {
        String ipAddress = socket.getRemoteSocketAddress().toString();
        LOGGER.info("Connected to client with ipAddress: " + ipAddress);

        ClientInputListener clientInputListener = new ClientInputListener(socket,
                inputPacketGateway,
                clientPacketCoder,
                ipAddress,
                connectionPool,
                serverPingPongRunner);
        clientInputListener.setUsernameReceivedListener(username -> {
            // only start output connection, when username received
            ClientOutput clientOutput = new ClientOutput(socket, ipAddress, serverPacketCoder, connectionPool);
            connectionPool.addConnection(ipAddress, new Connection(socket, clientOutput, clientInputListener));
            // username counts as PONG, because this means, that the client is connected
            serverPingPongRunner.setPongArrived(ipAddress);
            clientOutput.sendPacketToClient(new Packet.Server(PING, null, NONE));
        });

        Thread t = new Thread(clientInputListener);
        clientInputListener.setThread(t);
        t.start();
    }
}
