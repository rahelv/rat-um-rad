package ch.progradler.rat_um_rad.server.gateway;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.List;

/**
 * Interface which allows sending messages to one or more clients.
 */
public interface OutputPacketGateway {
    void sendPacket(String ipAddress, Packet packet);

    /**
     * @param excludeClients list of ipAddresses to which packet should not be broadcast.
     */
    void broadCastExclude(Packet packet, List<String> excludeClients);

    /**
     * @param clients list of ipAddress to which the packet should be broadcast
     */
    void broadCastOnly(Packet packet, List<String> clients);
}
