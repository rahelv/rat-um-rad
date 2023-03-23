package ch.progradler.rat_um_rad.shared.protocol;

/**
 * Possible commands of a packet.
 */
public enum Command {
    NEW_USER,
    USERNAME_CONFIRMED,
    CHANGED_USERNAME,
    USER_DISCONNECTED,
    SEND_CHAT,
    CLIENT_DISCONNECTED,
}
