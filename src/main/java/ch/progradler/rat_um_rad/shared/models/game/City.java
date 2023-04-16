package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;

import java.util.Objects;

/**
 * Implementation of the cities occurring in the {@link GameMap}, {@link Road} and {@link DestinationCard}.
 */
public class City {
    private final String id;
    private final String name;
    private final Point point;

    public City(String id, String name, Point point) {
        this.id = id;
        this.name = name;
        this.point = point;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return id.equals(city.id) && name.equals(city.name) && point.equals(city.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, point);
    }
}
