package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Holds input and output of a client connection.
 */
public class Connection {
    private final ClientOutput clientOutput;
    private final ClientInputListener clientInputListener;

    public Connection(ClientOutput clientOutput, ClientInputListener clientInputListener) {
        this.clientOutput = clientOutput;
        this.clientInputListener = clientInputListener;
    }

    public void sendMessageToClient(Packet packet) {
        clientOutput.sendMessageToClient(packet);
    }
}
