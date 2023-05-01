package ch.progradler.rat_um_rad.server.models;

/**
 * Holds data about an in game player's action.
 */
public class Action<T> {
    public final Game game;
    public final String ipAddress;
    public final T actionData;

    public Action(Game game, String ipAddress, T actionData) {
        this.game = game;
        this.ipAddress = ipAddress;
        this.actionData = actionData;
    }
}
