package ch.progradler.rat_um_rad.shared.models.game;

/**
 * Status of a game instance.
 */
public enum GameStatus {
    WAITING_FOR_PLAYERS,
    /**
     * When separate the games into open, started and finished, PREPARATION falls under started.
     */
    PREPARATION,
    STARTED,
    FINISHED
}
