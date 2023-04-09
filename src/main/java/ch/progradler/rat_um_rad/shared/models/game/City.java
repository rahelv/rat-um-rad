package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;

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
}
