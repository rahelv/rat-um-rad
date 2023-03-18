package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Allows easy output to a client with certain {@link ClientOutput#ipAddress} via the {@link ClientOutput#out} stream.
 */
public class ClientOutput {
    private Socket socket;
    private ObjectOutputStream out; //TODO: implement using own serialization
    private final String ipAddress;

    public ClientOutput(Socket socket, String ipAddress) {
        this.socket = socket;
        this.ipAddress = ipAddress;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace(); //TODO: error management
        }
    }

    public void sendMessageToClient(Packet packet) {
        try {
            out.writeObject(packet); // TODO: encode
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
