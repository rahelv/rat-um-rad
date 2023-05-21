package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.io.IOException;

/**
 * Interface of a connection.
 */
public interface IConnection {
    void sendPacketToClient(Packet<ServerCommand> packet);

    void close() throws IOException;
    Thread getThread();
}
