package ch.progradler.rat_um_rad.client.gateway;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Interface which allows handling of incoming packet from server.
 */
public interface ServerInputPacketGateway {
     void handleResponse(Packet packet);
}
