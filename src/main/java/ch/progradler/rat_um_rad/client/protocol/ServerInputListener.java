package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Listens to messages from server
 */
public class ServerInputListener implements Runnable {
    private final ServerInputPacketGateway inputPacketGateway;
    private final Coder<Packet<ServerCommand>> packetCoder;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private static final Logger LOGGER = LogManager.getLogger();

    public ServerInputListener(Socket socket, ServerInputPacketGateway inputPacketGateway, Coder<Packet<ServerCommand>> packetCoder) {
        this.inputPacketGateway = inputPacketGateway;
        this.packetCoder = packetCoder;
        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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
                encodedPacket = StreamUtils.readStringsFromStream(bufferedReader);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.info("There was an IOException while trying to readStringsFromStream");
                continue;
                // TODO: display error to user?
            }

            if (encodedPacket == null) {
                //don't log since if the server sended null, it's logged there and if the server didn't and still there is null read, this means that the socket was closed on server side
                continue;
            }

            Packet<ServerCommand> packet = packetCoder.decode(encodedPacket, 0);
            if (encodedPacket == null) {
                LOGGER.info("The encoded packet "+encodedPacket+" was null after decoding.");
                //printed in console so that developers see that
                System.out.println("\033[0;31m" + "The encoded packet "+encodedPacket+" was null after decoding." + "\033[0m");
                continue;
            }
            inputPacketGateway.handleResponse(packet);
        }
    }
}

