package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.UsernameReceivedListener;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Listens to incoming commands from a specific client via socket stream.
 */
public class ClientInputListener implements Runnable {
    private final InputPacketGateway inputPacketGateway;
    private Socket socket;
    private ObjectInputStream in; //TODO: implement using inputStream
    private String ipAddress;
    private UsernameReceivedListener usernameReceivedListener;

    public ClientInputListener(Socket socket, InputPacketGateway inputPacketGateway) {
        this.inputPacketGateway = inputPacketGateway;
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
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
            String username = (String) in.readObject(); //TODO: Sanitize input
            usernameReceivedListener.onUsernameReceived(username);
            inputPacketGateway.handleClientCommand(new Packet(Command.NEW_USER, username, ContentType.USERNAME), ipAddress);

            while (true) {
                Packet packet = (Packet) in.readObject(); //TODO: in.read() and then use own serialization method (decode)
                inputPacketGateway.handleClientCommand(packet, ipAddress); // TODO: unittest

                //TODO: implement QUIT scenario (with break)
                //important to remove client from pool so server doesn't crash
                //TODO: first, broadcast messages

                //TODO: later, implement network protocol and chose action accordingly
            }
        } catch (Exception e) {
            // TODO: remove in stream and socket connection for this client?
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return ipAddress;
    }
}
