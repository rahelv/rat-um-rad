package ch.progradler.rat_um_rad.client.protocol.pingpong;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner.TIME_FOR_DISCONNECT;

/**
 * Handles Client Ping Pong Logic
 */
public class ClientPingPongHandler {
    public static final Logger LOGGER = LogManager.getLogger();
    private final OutputPacketGateway outputPacketGateway;
    private boolean pingArrived = false;

    /**
     * @param outputPacketGateway is the serverOutput instance.
     */
    public ClientPingPongHandler(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    public boolean isPingArrived() {
        return pingArrived;
    }

    public void setPingArrived(boolean pingArrived) {
        this.pingArrived = pingArrived;
    }

    /**
     * If the time the server needed to return a PING lasts longer than TIME_FOR_DISCONNECT, the connection is broken up.
     */
    public void handleTimeForDisconnectOver(int arrivedPingCounter, int timeCounter) {
        if (timeCounter >= TIME_FOR_DISCONNECT) {
            LOGGER.log(Level.forName("PINGPONG", 700), arrivedPingCounter + ". PING didn't arrive within time limit.");
            //TODO break up connection
            return;
        }
        LOGGER.log(Level.forName("PINGPONG", 700), arrivedPingCounter + ". PING arrived");
        try {
            outputPacketGateway.sendPacket(new Packet.Client(ClientCommand.PONG, null, ContentType.NONE));
            LOGGER.log(Level.forName("PINGPONG", 700), arrivedPingCounter + ". PONG sent");
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Exception handling
        }
    }
}
