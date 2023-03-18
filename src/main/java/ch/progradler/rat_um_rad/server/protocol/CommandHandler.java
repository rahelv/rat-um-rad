package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.server.gateway.InputPacketGateway;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles incoming commands by clients.
 */
public class CommandHandler implements InputPacketGateway {

    private final OutputPacketGateway outputPacketGateway;

    public CommandHandler(OutputPacketGateway outputPacketGateway) {
        this.outputPacketGateway = outputPacketGateway;
    }

    public  void handleClientCommand(Packet packet, String ipAddress) {
        // TODO: change/adapt, unittest

        switch (packet.getCommand()) {
            case NEW_USER -> {
                List<String> excludeFromBroadCast = Collections.singletonList(ipAddress);
                outputPacketGateway.broadCast(packet, excludeFromBroadCast);
            }
            case SEND_ALL -> {
                outputPacketGateway.broadCast(packet, new ArrayList<>());
            }
            case SEND_ALL_EXCEPT_SENDER -> {
                List<String> excludeFromBroadCast = Collections.singletonList(ipAddress);
                outputPacketGateway.broadCast(packet, excludeFromBroadCast);
            }
        }

    }
}
