package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Responsible for sending packets to server via socket stream.
 */
public class ServerOutput implements OutputPacketGateway {
    final Socket socket;
    final ObjectOutputStream out;


    public ServerOutput(Socket socket) throws Exception {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Failed to init OutputSocket");
        }
    }

    @Override
    public  void sendPacket(Packet packet) throws IOException {
        out.writeObject(packet);
    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
    }
}
