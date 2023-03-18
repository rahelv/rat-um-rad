package ch.progradler.rat_um_rad.server.protocol;

/**
 * Interface which allows notification of username received.
 */
public interface UsernameReceivedListener {
    void onUsernameReceived(String username);
}
