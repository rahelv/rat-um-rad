package ch.progradler.rat_um_rad.shared.protocol;

/**
 * Commands sent from Client to Server
 */
public enum ClientCommand {
    SEND_BROADCAST_CHAT,
    SEND_GAME_INTERNAL_CHAT,
    SEND_WHISPER_CHAT,
    REGISTER_USER,
    /**
     * Is regularly sent from client to server in order to detect connection losses. Expects the server to send back a PING command.
     */
    PONG,
    USER_SOCKET_DISCONNECTED,
    SET_USERNAME,
    CREATE_GAME,
    REQUEST_GAMES,
    REQUEST_ALL_CONNECTED_PLAYERS,
    WANT_JOIN_GAME,
    SHORT_DESTINATION_CARDS_SELECTED,
    REQUEST_SHORT_DESTINATION_CARDS,
    BUILD_ROAD,
    REQUEST_WHEEL_CARDS,
}
