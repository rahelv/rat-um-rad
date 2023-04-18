package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Listens to messages from server
 */
public class ServerInputListener implements Runnable {
    private InputStream inputStream;
    private final ServerInputPacketGateway inputPacketGateway;
    private final Coder<Packet<ServerCommand>> packetCoder;

    public ServerInputListener(Socket socket, ServerInputPacketGateway inputPacketGateway, Coder<Packet<ServerCommand>> packetCoder) {
        this.inputPacketGateway = inputPacketGateway;
        this.packetCoder = packetCoder;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            // TODO: handle?
            e.printStackTrace();
        }
    }

    /**
     * run method for the ServerInputListener. Listens for Packets from the Server.
     */
    @Override
    public void run() {
        while (true) { //so it keeps listening
            String encodedPacket;
            try {
                encodedPacket = StreamUtils.readStringFromStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
                // TODO: display error to user?
            }
            Packet<ServerCommand> packet = packetCoder.decode(encodedPacket, 0);
            inputPacketGateway.handleResponse(packet);
        }
    }
}

