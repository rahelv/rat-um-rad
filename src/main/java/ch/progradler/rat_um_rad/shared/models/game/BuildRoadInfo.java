package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.Objects;

/**
 * Model class for client to send to server, when a grey road should be built.
 */
public class BuildRoadInfo {
    private final String roadId;
    private final WheelColor color;

    public BuildRoadInfo(String roadId, WheelColor color) {
        this.roadId = roadId;
        this.color = color;
    }

    public String getRoadId() {
        return roadId;
    }

    public WheelColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildRoadInfo that = (BuildRoadInfo) o;
        return roadId.equals(that.roadId) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roadId, color);
    }
}
