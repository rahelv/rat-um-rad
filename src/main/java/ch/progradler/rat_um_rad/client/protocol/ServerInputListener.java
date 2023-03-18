package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Listens to messages from server
 */
public class ServerInputListener implements Runnable {
    private Socket socket;
    private ObjectInputStream in; //TODO: implement using inputStream

    private final ServerInputPacketGateway inputPacketGateway;

    public ServerInputListener(Socket socket, ServerInputPacketGateway inputPacketGateway) {
        this.socket = socket;
        this.inputPacketGateway = inputPacketGateway;
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            // TODO: handle?
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) { //so it keeps listening
                Packet response = (Packet) in.readObject();
                inputPacketGateway.handleResponse(response);

                //TODO: implement QUIT command and other commands
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

