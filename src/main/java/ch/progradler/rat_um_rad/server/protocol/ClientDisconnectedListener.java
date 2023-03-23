package ch.progradler.rat_um_rad.server.protocol;

/**
 * Interface which allows notification of disconnection event.
 */
public interface ClientDisconnectedListener {
    void onDisconnected(String ipAddress);
}
