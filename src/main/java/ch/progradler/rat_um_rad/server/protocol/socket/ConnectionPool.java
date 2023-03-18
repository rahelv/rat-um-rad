package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all client connections.
 */
public class ConnectionPool implements OutputPacketGateway {
    /**
     * Keys are IP-Addresses.
     */
    private final Map<String, Connection> connections = new HashMap<>();

    public void addConnection(String ipAddress, Connection connection) {
        connections.put(ipAddress, connection);
    }

    public void removeConnectionByName(String ipAddress) {
        connections.remove(ipAddress);
    }

    @Override
    public  void sendMessage(String ipAddress, Packet packet) {
        // TODO: unittest
        Connection connection = connections.get(ipAddress);
        connection.sendMessageToClient(packet);
    }

    @Override
    public  void broadCast(Packet packet, List<String> excludeClients) {
        // TODO: unittest
        final List<String> clientsForBroadCast = new ArrayList<>(connections.keySet());
        clientsForBroadCast.removeAll(excludeClients);

        for (String ipAddress : clientsForBroadCast) {
            sendMessage(ipAddress, packet);
        }
    }
}
