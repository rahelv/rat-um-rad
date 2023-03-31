package ch.progradler.rat_um_rad.shared.models.game;

/**
 * Implementation of the "Radstrecken".
 */
public class Road {
    private final String id;
    private final String fromCityId;
    private final String toCityId;
    private final int requiredWheels;
    private final int color;

    // TODO: String builtBy (null oder playerId)?

    public Road(String id, String fromCityId, String toCityId, int requiredWheels, int color) {
        this.id = id;
        this.fromCityId = fromCityId;
        this.toCityId = toCityId;
        this.requiredWheels = requiredWheels;
        this.color = color;
    }
}
