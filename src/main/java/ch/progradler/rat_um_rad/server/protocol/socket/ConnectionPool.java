package ch.progradler.rat_um_rad.server.protocol.socket;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.ClientDisconnectedListener;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all client connections.
 */
public class ConnectionPool implements OutputPacketGateway, ClientDisconnectedListener {
    /**
     * Keys are IP-Addresses.
     */
    private final Map<String, IConnection> connections = new HashMap<>();

    public void addConnection(String ipAddress, IConnection connection) {
        connections.put(ipAddress, connection);
    }

    @Override
    public void sendMessage(String ipAddress, Packet packet) {
        IConnection connection = connections.get(ipAddress);
        connection.sendMessageToClient(packet);
    }

    @Override
    public void broadCast(Packet packet, List<String> excludeClients) {
        final List<String> clientsForBroadCast = new ArrayList<>(connections.keySet());
        clientsForBroadCast.removeAll(excludeClients);

        for (String ipAddress : clientsForBroadCast) {
            sendMessage(ipAddress, packet);
        }
    }

    public void removeConnection(String ipAddress) {
        IConnection connection = connections.get(ipAddress);
        if (connection == null) return;

        try {
            connection.close();
            // only remove if successfully closed connection
            connections.remove(ipAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected(String ipAddress) {
        removeConnection(ipAddress);
    }
}
