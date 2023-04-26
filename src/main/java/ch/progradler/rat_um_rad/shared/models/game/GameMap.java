package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.util.Arrays;
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
        City luzern = new City(CityId.LUZERN, CityId.LUZERN, new Point(60, 65));
        City basel = new City(CityId.BASEL, CityId.BASEL, new Point(40, 35));
        City zurich = new City(CityId.ZURICH, CityId.ZURICH, new Point(60, 35));
        City bern = new City(CityId.BERN, CityId.BERN, new Point(40, 65));
        City neuchatel = new City(CityId.NEUCHATEL, CityId.NEUCHATEL, new Point(20, 20));
        City genf = new City(CityId.GENF, CityId.GENF, new Point(20, 80));
        City chur = new City(CityId.CHUR, CityId.CHUR, new Point(80, 20));
        City lugano = new City(CityId.LUGANO, CityId.LUGANO, new Point(80, 80));
        List<City> cities = Arrays.asList(luzern, basel, zurich, bern, basel, neuchatel, genf, chur, lugano);
        //TODO: implement correctly

        List<Road> roads = Arrays.asList(
                new Road("fromBaselToZürich", basel.getId(), zurich.getId(), 3, WheelColor.BLUE),
                new Road("fromZürichToLuzern", zurich.getId(), luzern.getId(), 2, WheelColor.RED),
                new Road("fromLuzernToBern", luzern.getId(), bern.getId(), 4, WheelColor.YELLOW),
                new Road("fromBernToBasel", bern.getId(), basel.getId(), 2, WheelColor.ORANGE),
                new Road("fromBaselToLuzern", basel.getId(), luzern.getId(), 4, WheelColor.PINK),
                new Road("fromBernToGenf", bern.getId(), genf.getId(), 3, WheelColor.BLUE),
                new Road("fromGenfToNeuchatel", genf.getId(), neuchatel.getId(), 2, WheelColor.PINK),
                new Road("fromNeuchatelToBasel", neuchatel.getId(), basel.getId(), 4, WheelColor.GREEN),
                new Road("fromNeuchatelToBern", neuchatel.getId(), bern.getId(), 4, WheelColor.RED),
                new Road("fromGenfToLuzern", genf.getId(), luzern.getId(), 3, WheelColor.WHITE),
                new Road("fromLuganoToGenf", lugano.getId(), genf.getId(), 3, WheelColor.YELLOW),
                new Road("fromChurToBasel", chur.getId(), basel.getId(), 3, WheelColor.ORANGE),
                new Road("fromChurToNeuchatel", chur.getId(), neuchatel.getId(), 3, WheelColor.YELLOW),
                new Road("fromZürichToChur", zurich.getId(), chur.getId(), 3, WheelColor.BLACK),
                new Road("fromChurToLugano", chur.getId(), lugano.getId(), 3, WheelColor.GREEN),
                new Road("fromLuganoToLuzern", lugano.getId(), luzern.getId(), 3, WheelColor.YELLOW),
                new Road("fromZurichToLugano", zurich.getId(), lugano.getId(), 3, WheelColor.PINK)
        );

        return new GameMap(cities, roads);
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
