package ch.progradler.rat_um_rad.server.protocol.pingpong;

import ch.progradler.rat_um_rad.server.protocol.socket.ConnectionPool;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static ch.progradler.rat_um_rad.shared.util.TimeUtils.SECOND;

/**
 * Sends regularly ping-messages to the clients and detects whether they return
 * pong-messages or not. If not, the client is removed from connections.
 */
public class ServerPingPongRunner implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Time in seconds until a client is seen as disconnected
     */
    private static final int TIME_FOR_DISCONNECT = 8;
    private final ServerPingPongHandler pingPongHandler;


    public ServerPingPongRunner(ConnectionPool connectionPool) {
        this.pingPongHandler = new ServerPingPongHandler(connectionPool);
    }

    /**
     * CommandHandler uses this method every time a pong-message arrives.
     */
    public void setPongArrived(String ipAddress) {
        synchronized (pingPongHandler) {
            pingPongHandler.setPongArrived(ipAddress);
        }
    }

    /**
     * Sleeps {@link ServerPingPongRunner#TIME_FOR_DISCONNECT} seconds and removes each connection
     * of which no pong-message arrived.
     */
    @Override
    public void run() {
        int pingSentCount = 0;
        while (true) {
            LOGGER.log(Level.forName("PINGPONG", 700), "Ping sent count: " + pingSentCount);
            waitTimeForDisconnect();
            synchronized (pingPongHandler) {
                pingPongHandler.checkDisconnections(pingSentCount);
            }
            pingSentCount++;
        }
    }

    /**
     * Current thread sleeps for {@link ServerPingPongRunner#TIME_FOR_DISCONNECT} seconds.
     */
    private void waitTimeForDisconnect() {
        try {
            Thread.sleep(TIME_FOR_DISCONNECT * SECOND);
        } catch (InterruptedException e) {
            LOGGER.warn("Failed to wait time for disconnect.", e);
        }
    }
}