package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.Objects;

/**
 * Implementation of the "Radstrecken".
 */
public class Road {
    private final String id;
    private final String fromCityId;
    private final String toCityId;
    private final int requiredWheels;
    private final WheelColor color;

    public Road(String id, String fromCityId, String toCityId, int requiredWheels, WheelColor color) {
        this.id = id;
        this.fromCityId = fromCityId;
        this.toCityId = toCityId;
        this.requiredWheels = requiredWheels;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getFromCityId() {
        return fromCityId;
    }

    public String getToCityId() {
        return toCityId;
    }

    public int getRequiredWheels() {
        return requiredWheels;
    }

    public WheelColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Road)) return false;
        Road road = (Road) o;
        return requiredWheels == road.requiredWheels && id.equals(road.id) && fromCityId.equals(road.fromCityId) && toCityId.equals(road.toCityId) && color == road.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromCityId, toCityId, requiredWheels, color);
    }
}
