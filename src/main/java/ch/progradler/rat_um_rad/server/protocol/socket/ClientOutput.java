package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Allows easy output to a client with certain {@link ClientOutput#ipAddress} via the {@link ClientOutput#out} stream.
 */
public class ClientOutput {
    private Socket socket;
    private OutputStream out; //TODO: implement using own serialization
    private final String ipAddress;
    private final Coder<Packet<ServerCommand>> packetCoder;

    public ClientOutput(Socket socket, String ipAddress, Coder<Packet<ServerCommand>> packetCoder) {
        this.socket = socket;
        this.packetCoder = packetCoder;
        this.ipAddress = ipAddress;
        try {
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace(); //TODO: error management
        }
    }

    public void sendPacketToClient(Packet<ServerCommand> packet) {
        String sendStr = packetCoder.encode(packet, 0);
        //System.out.println("packet send: " +  sendStr);
        StreamUtils.writeStringToStream(sendStr, out);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Closes output stream.
     */
    public void close() throws IOException {
        out.close();
    }
}
