package ch.progradler.rat_um_rad.server.protocol.pingpong;

import ch.progradler.rat_um_rad.server.protocol.socket.ConnectionPool;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.PING;

/**
 * Handler for Server Ping Pong Logic
 */
public class ServerPingPongHandler {
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Holds a boolean value whether or not a pong hast arrived within disconnection time
     * for each client (distinguished by ipAddress as key)
     */
    private final Map<String, Boolean> pongArrived = new HashMap<>();
    private final ConnectionPool connectionPool;

    public ServerPingPongHandler(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Checks for disconnected clients and handles appropriately.
     *
     * @see ServerPingPongHandler#handleConnectedClients(int, List)} and {@link ServerPingPongHandler#handleRemoveConnections(int, List)}
     */
    public void checkDisconnections(int pingSentCount) {
        Collection<String> ipAddresses = pongArrived.keySet();
        List<String> clientsWithPongReceived = ipAddresses.stream()
                .filter(pongArrived::get).toList();
        List<String> clientsWithNoPongReceived = ipAddresses.stream()
                .filter(ipAddress -> !pongArrived.get(ipAddress)).toList();

        handleRemoveConnections(pingSentCount, clientsWithNoPongReceived);
        handleConnectedClients(pingSentCount, clientsWithPongReceived);
    }

    /**
     * @param clientsWithPongReceived: the ipAddresses of the clients, from which a pong was received within certain time.
     *                                 A Ping is resent to those clients and {@link ServerPingPongHandler#pongArrived} is set to false for those clients.
     */
    private void handleConnectedClients(int pingSentCount, List<String> clientsWithPongReceived) {
        for (String ipAddress : clientsWithPongReceived) {
            LOGGER.log(Level.forName("PINGPONG", 700), "{}. PONG arrived from {}", pingSentCount, ipAddress);
            pongArrived.put(ipAddress, false);
            connectionPool.sendPacket(ipAddress, new Packet.Server(PING, null, ContentType.NONE));
            LOGGER.log(Level.forName("PINGPONG", 700), "{}. PING sent to {}", pingSentCount, ipAddress);
        }
    }

    /**
     * @param clientsWithNoPongReceived: the ipAddresses of the clients, from which no pong was received within certain time.
     *                                   Removes those client connections
     */
    private void handleRemoveConnections(int pingSentCount, List<String> clientsWithNoPongReceived) {
        for (String ipAddress : clientsWithNoPongReceived) {
            LOGGER.log(Level.forName("PINGPONG", 700), "{}. PONG didn't arrive from {}", pingSentCount, ipAddress);
            connectionPool.removeConnection(ipAddress);
            pongArrived.remove(ipAddress);
        }
    }

    public void setPongArrived(String ipAddress) {
        pongArrived.put(ipAddress, true);
    }
}
