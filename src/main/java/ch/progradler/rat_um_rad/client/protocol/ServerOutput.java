package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.util.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Responsible for sending packets to server via socket stream.
 */
public class ServerOutput implements OutputPacketGateway {
    private final Socket socket;
    private final OutputStream outStream;
    private final Coder<Packet> packetCoder;

    public ServerOutput(Socket socket, Coder<Packet> packetCoder) throws Exception {
        this.socket = socket;
        this.packetCoder = packetCoder;
        try {
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to init OutputSocket");
        }
    }

    /**
     * Sends packet to the server.
     * @param packet
     * @throws IOException
     */
    @Override
    public  void sendPacket(Packet packet) throws IOException {
        //TODO: unittest
        String sendStr = packetCoder.encode(packet);
        StreamUtils.writeStringToStream(sendStr,outStream);
    }
}
