package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.io.IOException;
import java.net.Socket;

/**
 * Holds input and output of a client connection.
 */
public class Connection implements IConnection {
    private final Socket socket;
    private final ClientOutput clientOutput;
    private final ClientInputListener clientInputListener;

    public Connection(Socket socket, ClientOutput clientOutput, ClientInputListener clientInputListener) {
        this.socket = socket;
        this.clientOutput = clientOutput;
        this.clientInputListener = clientInputListener;
    }

    public void sendPacketToClient(Packet<ServerCommand> packet) {
        clientOutput.sendPacketToClient(packet);
    }

    public void close() throws IOException {
        clientOutput.close();
        clientInputListener.close();
        socket.close();
    }
    @Override
    public Thread getThread() {
        return clientInputListener.getThread();
    }
}
