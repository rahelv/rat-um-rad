package ch.progradler.rat_um_rad.shared.protocol;

/**
 * Commands sent from Server to Client
 */
public enum ServerCommand {
    BROADCAST_CHAT_SENT,
    GAME_INTERNAL_CHAT_SENT,
    WHISPER_CHAT_SENT,
    NEW_USER,
    CHANGED_USERNAME,
    USERNAME_CONFIRMED,
    USER_DISCONNECTED,
    GAME_CREATED,
    GAME_UPDATED,
    SEND_ALL_CONNECTED_PLAYERS,
    /**
     * Is regularly sent from server to client in order to detect connection losses. Expects the client to send back a PONG command.
     */
    PING,
    INVALID_ACTION_WARNING,
    INVALID_ACTION_FATAL,
    SEND_WAITING_GAMES,
    SEND_STARTED_GAMES,
    SEND_FINISHED_GAMES,
    GAME_JOINED,
    NEW_PLAYER,
    GAME_STARTED_SELECT_DESTINATION_CARDS,
    REQUEST_SHORT_DESTINATION_CARDS_RESULT,
    DESTINATION_CARDS_SELECTED,
    ROAD_BUILT,
    WHEEL_CARDS_CHOSEN,
    GAME_ENDED,
}