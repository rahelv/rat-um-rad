package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

/**
 * Interface of a connection.
 */
public interface IConnection {
    void sendMessageToClient(Packet packet);

    void close() throws IOException;
}
