package ch.progradler.rat_um_rad.client.command_line.presenter;

import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

/**
 * Interface which allows correct displaying of packet.
 */
public interface PackagePresenter {
    void display(Packet<ServerCommand> packet);
}
