package ch.progradler.rat_um_rad.shared.models.game;

import java.util.ArrayList;
import java.util.List;

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
        return new GameMap(new ArrayList<>(), new ArrayList<>()); // TODO: implement correctly
    }

    public List<City> getCities() {
        return cities;
    }

    public List<Road> getRoads() {
        return roads;
    }
}
