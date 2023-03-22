package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.UsernameReceivedListener;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


/**
 * Listens to incoming commands from a specific client via socket stream.
 */
public class ClientInputListener implements Runnable {
    private final InputPacketGateway inputPacketGateway;
    private Socket socket;
    private InputStream inputStream;
    private final Coder<Packet> packetCoder;
    private String ipAddress;
    private UsernameReceivedListener usernameReceivedListener;

    public ClientInputListener(Socket socket, InputPacketGateway inputPacketGateway, Coder<Packet> packetCoder) {
        this.inputPacketGateway = inputPacketGateway;
        this.socket = socket;
        this.packetCoder = packetCoder;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace(); //TODO: error management
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

                //TODO: later, implement network protocol and chose action accordingly
            }
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

    public String getClientName() {
        return ipAddress;
    }
}
