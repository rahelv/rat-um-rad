package ch.progradler.rat_um_rad.shared.protocol;

/**
 * Possible commands of a packet.
 */
public enum Command {
    // shared commands: (sent from and to server)

    SEND_BROADCAST_CHAT,
    SEND_GAME_INTERNAL_CHAT,
    SEND_WHISPER_CHAT,
    NEW_USER,

    // client commands (sent from client to server):

    /**
     * Is regularly sent from client to server in order to detect connection losses. Expects the server to send back a PING command.
     */
    PONG,
    SET_USERNAME,
    CREATE_GAME,
    REQUEST_GAMES,
    REQUEST_ALL_CONNECTED_PLAYERS,
    WANT_JOIN_GAME,
    SHORT_DESTINATION_CARDS_SELECTED_IN_PREPARATION,
    REQUEST_SHORT_DESTINATION_CARDS,
    BUILD_ROAD,

    // server commands (sent from server to client):
    CHANGED_USERNAME,
    USERNAME_CONFIRMED,
    USER_DISCONNECTED,
    GAME_CREATED,
    SEND_ALL_CONNECTED_PLAYERS,
    /**
     * Is regularly sent from server to client in order to detect connection losses. Expects the client to send back a PONG command.
     */
    PING,
    INVALID_ACTION_WARNING,
    INVALID_ACTION_FATAL,
    SEND_GAMES,
    SEND_WAITING_GAMES,
    SEND_STARTED_GAMES,
    SEND_FINISHED_GAMES,
    GAME_JOINED,
    NEW_PLAYER,
    GAME_STARTED_SELECT_DESTINATION_CARDS,

}
