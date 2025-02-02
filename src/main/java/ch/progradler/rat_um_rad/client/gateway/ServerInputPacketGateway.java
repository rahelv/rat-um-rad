package ch.progradler.rat_um_rad.client.gateway;

import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

/**
 * Interface which allows handling of incoming packet from server.
 */
public interface ServerInputPacketGateway {
    void handleResponse(Packet<ServerCommand> packet);

    void addListener(ServerResponseListener<?> listenerToAdd);
}
