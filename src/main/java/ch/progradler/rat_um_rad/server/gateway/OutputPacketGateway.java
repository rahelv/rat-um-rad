package ch.progradler.rat_um_rad.server.gateway;

import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.util.List;

/**
 * Interface which allows sending messages to one or more clients.
 */
public interface OutputPacketGateway {
    void sendPacket(String ipAddress, Packet<ServerCommand> packet);

    /**
     * @param excludeClients list of ipAddresses to which packet should not be broadcast.
     */
    void broadCastExclude(Packet<ServerCommand> packet, List<String> excludeClients);

    /**
     * @param clients list of ipAddress to which the packet should be broadcast
     */
    void broadCastOnly(Packet<ServerCommand> packet, List<String> clients);

    /**
     * Broadcasts the packet to all connected users.
     *
     * @param packet
     */
    void broadcast(Packet<ServerCommand> packet);
}
