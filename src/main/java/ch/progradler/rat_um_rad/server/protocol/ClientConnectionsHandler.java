package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.socket.ClientInputListener;
import ch.progradler.rat_um_rad.server.protocol.socket.ClientOutput;
import ch.progradler.rat_um_rad.server.protocol.socket.Connection;
import ch.progradler.rat_um_rad.server.protocol.socket.ConnectionPool;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Manages client connections and starts process of listening for client connection requests.
 */
public class ClientConnectionsHandler {
    public final ConnectionPool connectionPool = new ConnectionPool();
    private final Coder<Packet> packetCoder;

    public ClientConnectionsHandler(Coder<Packet> packetCoder) {
        this.packetCoder = packetCoder;
    }

    /**
     * Starts listening for incoming client connection requests.
     */
    public void start(int port, InputPacketGateway inputPacketGateway) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.format("Server listening on port %d\n", port);

            while (true) { //keeps running
                acceptNewClient(inputPacketGateway, serverSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptNewClient(InputPacketGateway inputPacketGateway, ServerSocket serverSocket) {
        Socket socket;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String ipAddress = socket.getRemoteSocketAddress().toString();
        System.out.println("Connected to client with ipAddress: " + ipAddress);

        ClientInputListener clientInputListener = new ClientInputListener(socket, inputPacketGateway, packetCoder);
        clientInputListener.setUsernameReceivedListener(username -> {
            // only start output connection, when username received
            ClientOutput clientOutput = new ClientOutput(socket, ipAddress,packetCoder);
            connectionPool.addConnection(ipAddress, new Connection(clientOutput, clientInputListener));
        });

        Thread t = new Thread(clientInputListener);
        t.start();
    }
}
