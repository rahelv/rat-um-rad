package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Listens to messages from server
 */
public class ServerInputListener implements Runnable {
    private Socket socket;
    private InputStream inputStream;

    private final ServerInputPacketGateway inputPacketGateway;
    private final Coder<Packet> packetCoder;


    public ServerInputListener(Socket socket, ServerInputPacketGateway inputPacketGateway, Coder<Packet> packetCoder) {
        this.socket = socket;
        this.inputPacketGateway = inputPacketGateway;
        this.packetCoder = packetCoder;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            // TODO: handle?
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) { //so it keeps listening
            String encodedPacket = null;
            try {
                encodedPacket = StreamUtils.readStringFromStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: display error to user?
            }
            if (encodedPacket == null) continue;
            Packet packet = packetCoder.decode(encodedPacket);
            inputPacketGateway.handleResponse(packet);
        }
    }
}

