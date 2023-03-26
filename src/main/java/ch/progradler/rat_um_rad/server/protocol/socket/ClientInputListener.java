package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.ClientDisconnectedListener;
import ch.progradler.rat_um_rad.server.protocol.UsernameReceivedListener;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Listens to incoming commands from a specific client via socket stream.
 */
public class ClientInputListener implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger();
    private final InputPacketGateway inputPacketGateway;
    private InputStream inputStream;
    private final Coder<Packet> packetCoder;
    private String ipAddress;
    private final ClientDisconnectedListener clientDisconnectedListener;
    private UsernameReceivedListener usernameReceivedListener;
    private final ServerPingPongRunner serverPingPongRunner;

    public ClientInputListener(Socket socket,
                               InputPacketGateway inputPacketGateway,
                               Coder<Packet> packetCoder,
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
        } catch (IOException e) {
            e.printStackTrace(); //TODO: error management
            LOGGER.warn("Failed to connect with client. Removing client...");
            clientDisconnectedListener.onDisconnected(ipAddress);
        }

    }

    public void setUsernameReceivedListener(UsernameReceivedListener usernameReceivedListener) {
        this.usernameReceivedListener = usernameReceivedListener;
    }

    @Override
    public void run() {
        try {
            //expects the username first
            readUsername();

            while (true) {
                String encoded = StreamUtils.readStringFromStream(inputStream);
                Packet packet = packetCoder.decode(encoded);
                inputPacketGateway.handleClientCommand(packet, ipAddress);
                // TODO: unittest

                //TODO: implement QUIT scenario (with break)
                //important to remove client from pool so server doesn't crash
                //TODO: first, broadcast messages

                //TODO: later, implement network protocol and chose action accordingly
            }
        } catch (SocketException e) {
            inputPacketGateway.handleClientCommand(new Packet(Command.USER_DISCONNECTED,
                    null,
                    ContentType.NONE), ipAddress);
            System.out.println("Client " + ipAddress +
                    " disconnected from socket connection!");
            clientDisconnectedListener.onDisconnected(ipAddress);
        } catch (Exception e) {
            // TODO: remove in stream and socket connection for this client?
            e.printStackTrace();
        }
    }

    private void readUsername() throws IOException {
        String usernamePacketEncoded = StreamUtils.readStringFromStream(inputStream);
        Packet usernamePacket = packetCoder.decode(usernamePacketEncoded);
        String username = (String) usernamePacket.getContent();
        //TODO: Validate username
        usernameReceivedListener.onUsernameReceived(username);
        inputPacketGateway.handleClientCommand(usernamePacket, ipAddress);
    }

    /**
     * Closes input stream.
     */
    public void close() throws IOException {
        // TODO: close thread in which client input listener is running
        inputStream.close();
    }
}
