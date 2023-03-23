package ch.progradler.rat_um_rad.server.gateway;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.List;

/**
 * Interface which allows sending messages to one or more clients.
 */
public interface OutputPacketGateway {
    void sendMessage(String ipAddress, Packet packet);

    /**
     * @param excludeClients list of ipAddresses to which message should not be broadcast.
     */
    void broadCast(Packet packet, List<String> excludeClients);
}
