package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collection of the roads and cities. Needed for the GUI.
 */
public class GameMap {
    private final List<City> cities;
    private final List<Road> roads;

    public GameMap(List<City> cities, List<Road> roads) {
        this.cities = cities;
        this.roads = roads;
    }

    public static GameMap defaultMap() {
        return new GameMap(new ArrayList<>(), new ArrayList<Road>() {{
            add(new Road("fromLuzernToBasel", "Luzern", "Basel", 3, WheelColor.BLUE));
            add(new Road("fromBiozentrumToKollegienhaus", "Biozentrum", "Kollegienhaus", 2, WheelColor.BLUE));
            add(new Road("fromZurichtoLuzern", "Zurich", "Luzern", 5, WheelColor.GREEN));
            add(new Road("fromBaselToBern", "Basel", "Bern", 4, WheelColor.GREEN));
        }}); //TODO: implement correctly
    }

    public List<City> getCities() {
        return cities;
    }

    public List<Road> getRoads() {
        return roads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameMap)) return false;
        GameMap map = (GameMap) o;
        return cities.equals(map.cities) && roads.equals(map.roads);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cities, roads);
    }
}
