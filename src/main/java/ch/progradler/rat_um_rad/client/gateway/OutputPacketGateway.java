package ch.progradler.rat_um_rad.client.gateway;

import ch.progradler.rat_um_rad.shared.protocol.ClientCommand;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.io.IOException;

/**
 * Interface which allows sending a packet so server.
 */
public interface OutputPacketGateway {
    void sendPacket(Packet<ClientCommand> packet) throws IOException;
}