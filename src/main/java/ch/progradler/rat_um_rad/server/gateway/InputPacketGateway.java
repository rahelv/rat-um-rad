package ch.progradler.rat_um_rad.server.gateway;

import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Interface which allows handling incoming client command.
 */
public interface InputPacketGateway {
    void handleCommand(Packet<ClientCommand> packet, String ipAddress);
}
