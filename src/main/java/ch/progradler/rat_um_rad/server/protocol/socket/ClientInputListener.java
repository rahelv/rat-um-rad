package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.ClientDisconnectedListener;
import ch.progradler.rat_um_rad.server.protocol.UsernameReceivedListener;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * Listens to incoming commands from a specific client via socket stream.
 * For each client, there is one such input listener-instance and thus a {@link Thread}.
 */
public class ClientInputListener implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger();
    private final InputPacketGateway inputPacketGateway;
    private final Coder<Packet<ClientCommand>> packetCoder;
    private final ClientDisconnectedListener clientDisconnectedListener;
    private final ServerPingPongRunner serverPingPongRunner;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private String ipAddress;
    private UsernameReceivedListener usernameReceivedListener;
    private Thread thread; //Thread whose run method is in this class

    public ClientInputListener(Socket socket,
                               InputPacketGateway inputPacketGateway,
                               Coder<Packet<ClientCommand>> packetCoder,
                               String ipAddress,
                               ClientDisconnectedListener clientDisconnectedListener,
                               ServerPingPongRunner serverPingPongRunner) {
        this.inputPacketGateway = inputPacketGateway;
        this.packetCoder = packetCoder;
        this.ipAddress = ipAddress;
        this.clientDisconnectedListener = clientDisconnectedListener;
        this.serverPingPongRunner = serverPingPongRunner;
        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.warn("Failed to connect with client. Removing client...", e);
            clientDisconnectedListener.onDisconnected(ipAddress);
        }

    }

    public void setUsernameReceivedListener(UsernameReceivedListener usernameReceivedListener) {
        this.usernameReceivedListener = usernameReceivedListener;
    }

    /**
     * This method expects the username first and then starts listening. This is because the conneciton is only
     * set up when the username is recieved and thus, no further communication between server and client should be there.
     */
    @Override
    public void run() {
        try {
            readUsername();

            while (true) {
                String encodedPacket = StreamUtils.readStringsFromStream(bufferedReader);
                Packet<ClientCommand> packet = packetCoder.decode(encodedPacket, 0);
                inputPacketGateway.handleCommand(packet, ipAddress);
            }
        } catch (SocketException e) {
            inputPacketGateway.handleCommand(new Packet.Client(ClientCommand.USER_SOCKET_DISCONNECTED,
                    null,
                    ContentType.NONE), ipAddress);
            LOGGER.info("Client " + ipAddress + " disconnected from socket connection!");
            clientDisconnectedListener.onDisconnected(ipAddress);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * The method {@link UsernameReceivedListener#onUsernameReceived(String)} sets up the connection.
     * See in {@link ClientConnectionsHandler} for the concrete implementation.
     */
    private void readUsername() throws IOException {
        String usernamePacketEncoded = StreamUtils.readStringsFromStream(bufferedReader);

        Packet<ClientCommand> usernamePacket = packetCoder.decode(usernamePacketEncoded, 0);
        String username = (String) usernamePacket.getContent();
        usernameReceivedListener.onUsernameReceived(username);
        inputPacketGateway.handleCommand(usernamePacket, ipAddress);
    }

    /**
     * Closes input stream.
     */
    public void close() throws IOException {
        // TODO: close thread in which client input listener is running
        inputStream.close();
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }
}
