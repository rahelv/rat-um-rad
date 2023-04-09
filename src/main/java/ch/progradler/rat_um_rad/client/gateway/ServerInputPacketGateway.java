package ch.progradler.rat_um_rad.client.gateway;

import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.utils.listeners.IListener;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Interface which allows handling of incoming packet from server.
 */
public interface ServerInputPacketGateway {
     void handleResponse(Packet packet);

     void setUsernameChangeController(UsernameChangeController usernameChangeController);

     public void addListener(IListener listenerToAdd);
}
