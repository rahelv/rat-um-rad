package ch.progradler.rat_um_rad.shared.protocol;

/**
 * Possible commands of a packet.
 */
public enum Command {
    // shared commands:

    SEND_CHAT,
    NEW_USER,

    // client commands (sent from client to server):

    /**
     * Is regularly sent from client to server in order to detect connection losses. Expects the server to send back a PING command.
     */
    PONG,
    SET_USERNAME,
    CREATE_GAME,
    REQUEST_GAMES,

    // server commands (sent from server to client):
    CHANGED_USERNAME,
    USERNAME_CONFIRMED,
    USER_DISCONNECTED,
    GAME_CREATED,
    /**
     * Is regularly sent from server to client in order to detect connection losses. Expects the client to send back a PONG command.
     */
    PING,
    INVALID_ACTION_WARNING,
    INVALID_ACTION_FATAL,
    SEND_GAMES,

}
