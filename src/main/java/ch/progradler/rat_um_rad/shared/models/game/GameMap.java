package ch.progradler.rat_um_rad.shared.models.game;

import ch.progradler.rat_um_rad.shared.models.Point;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor.*;

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
        City pharma = new City(CityId.PHARMA, CityId.PHARMA, new Point(25, 40));
        City bibliothek = new City(CityId.BIBLIOTHEK, CityId.BIBLIOTHEK, new Point(15, 160));
        City philosophie = new City(CityId.PHILOSOPHIE, CityId.PHILOSOPHIE, new Point(20, 280));

        City deutsch = new City(CityId.DEUTSCH, CityId.DEUTSCH, new Point(80, 200));

        City biozentrum = new City(CityId.BIOZENTRUM, CityId.BIOZENTRUM, new Point(125, 20));
        City kollegienhaus = new City(CityId.KOLLEGIENHAUS, CityId.KOLLEGIENHAUS, new Point(150, 110));
        City theologie = new City(CityId.THEOLOGIE, CityId.THEOLOGIE, new Point(140, 170));
        City geschichte = new City(CityId.GESCHICHTE, CityId.GESCHICHTE, new Point(145, 270));

        City dmi = new City(CityId.DMI, CityId.DMI, new Point(220, 35));
        City sbb = new City(CityId.SBB, CityId.SBB, new Point(240, 190));
        City chemie = new City(CityId.CHEMIE, CityId.CHEMIE, new Point(240, 310));

        City biologie = new City(CityId.BIOLOGIE, CityId.BIOLOGIE, new Point(290, 30));
        City sport = new City(CityId.SPORT, CityId.SPORT, new Point(310, 110));
        City wwz = new City(CityId.WWZ, CityId.WWZ, new Point(320, 250));
        City jus = new City(CityId.JUS, CityId.JUS, new Point(360, 310));

        City psychologie = new City(CityId.PSYCHOLOGIE, CityId.PSYCHOLOGIE, new Point(420, 35));
        City kunstgeschichte = new City(CityId.KUNSTGESCHICHTE, CityId.KUNSTGESCHICHTE, new Point(400, 160));
        City powi = new City(CityId.POWI, CityId.POWI, new Point(460, 100));
        City medizin = new City(CityId.MEDIZIN, CityId.MEDIZIN, new Point(440, 300));
        City geographie = new City(CityId.GEOGRAPHIE, CityId.GEOGRAPHIE, new Point(460, 230));
        List<City> cities = Arrays.asList(pharma, bibliothek, philosophie, deutsch, biozentrum, kollegienhaus, theologie, geschichte, dmi, sbb, chemie, biologie, sport, wwz, jus, psychologie, kunstgeschichte, powi, medizin, geographie);
        //TODO: implement correctly

        List<Road> roads = Arrays.asList(
                new Road(pharma.getId(), biozentrum.getId(), 2, BLUE),
                new Road(pharma.getId(), kollegienhaus.getId(), 3, WHITE),
                new Road(pharma.getId(), bibliothek.getId(), 3, RED),
                new Road(bibliothek.getId(), deutsch.getId(), 1, ORANGE),
                new Road(bibliothek.getId(), philosophie.getId(), 3, BLACK),
                new Road(philosophie.getId(), deutsch.getId(), 2, BLUE),
                new Road(philosophie.getId(), geschichte.getId(), 3, GREEN),
                new Road(philosophie.getId(), chemie.getId(), 6, WHITE),
                new Road(deutsch.getId(), kollegienhaus.getId(), 3, RED),
                new Road(deutsch.getId(), theologie.getId(), 1, BLACK),
                new Road(deutsch.getId(), pharma.getId(), 5, GREEN),
                new Road(geschichte.getId(), theologie.getId(), 2, ORANGE),
                new Road(geschichte.getId(), sbb.getId(), 3, BLUE),
                new Road(geschichte.getId(), chemie.getId(), 2, YELLOW),
                new Road(biozentrum.getId(), dmi.getId(), 2, GREEN),
                new Road(biozentrum.getId(), kollegienhaus.getId(), 2, BLACK),
                new Road(dmi.getId(), kollegienhaus.getId(), 2, ORANGE),
                new Road(dmi.getId(), biologie.getId(), 1, BLACK),
                new Road(kollegienhaus.getId(), theologie.getId(), 1, GREEN),
                new Road(kollegienhaus.getId(), sbb.getId(), 3, BLACK),
                new Road(kollegienhaus.getId(), sport.getId(), 3, YELLOW),
                new Road(theologie.getId(), sbb.getId(), 2, YELLOW),
                new Road(sport.getId(), sbb.getId(), 2, RED),
                new Road(sport.getId(), biologie.getId(), 2, BLUE),
                new Road(sport.getId(), psychologie.getId(), 3, BLUE),
                new Road(sport.getId(), kunstgeschichte.getId(), 2, BLACK),
                new Road(sport.getId(), powi.getId(), 4, RED),
                new Road(biologie.getId(), psychologie.getId(), 3, RED),
                new Road(psychologie.getId(), powi.getId(), 1, BLUE),
                new Road(powi.getId(), kunstgeschichte.getId(), 2, ORANGE),
                new Road(powi.getId(), geographie.getId(), 3, YELLOW),
                new Road(kunstgeschichte.getId(), sbb.getId(), 4, GREEN),
                new Road(kunstgeschichte.getId(), geographie.getId(), 2, RED),
                new Road(kunstgeschichte.getId(), wwz.getId(), 2, BLUE),
                new Road(sbb.getId(), wwz.getId(), 2, WHITE),
                new Road(sbb.getId(), chemie.getId(), 3, BLACK),
                new Road(wwz.getId(), chemie.getId(), 2, RED),
                new Road(wwz.getId(), jus.getId(), 1, YELLOW),
                new Road(wwz.getId(), medizin.getId(), 3, GREEN),
                new Road(geographie.getId(), medizin.getId(), 1, ORANGE),
                new Road(jus.getId(), medizin.getId(), 1, BLACK),
                new Road(chemie.getId(), jus.getId(), 3, ORANGE)
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
