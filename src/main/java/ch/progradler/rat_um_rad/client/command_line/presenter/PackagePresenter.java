package ch.progradler.rat_um_rad.client.command_line.presenter;

import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Interface which allows correct displaying of packet.
 */
public interface PackagePresenter {
    void display(Packet packet);
}
