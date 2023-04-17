package ch.progradler.rat_um_rad.shared.models.game;

/**
 * Status of a game instance.
 */
public enum GameStatus {
    WAITING_FOR_PLAYERS,
    /**
     * When the games are separated into open, started and finished, PREPARATION falls under started.
     */
    PREPARATION,
    STARTED,
    FINISHED,
}
