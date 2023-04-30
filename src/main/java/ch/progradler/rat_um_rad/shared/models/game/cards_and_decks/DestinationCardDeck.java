package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.models.game.City;
import ch.progradler.rat_um_rad.shared.models.game.CityId;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of CardDeck with list of {@link DestinationCard}.
 */
public class DestinationCardDeck implements CardDeck {
    private final List<DestinationCard> cardDeck;

    public DestinationCardDeck(List<DestinationCard> cardDeck) {
        this.cardDeck = cardDeck;
    }

    /**
     * Factory Method to create deck with only short destination cards
     * @param map must be the {@link GameMap#defaultMap()}
     */
    public static DestinationCardDeck   shortDestinations(GameMap map) {
        Map<String, City> cities = new HashMap<>();
        map.getCities().forEach((city -> cities.put(city.getId(), city)));

        List<DestinationCard> destinationCards = new ArrayList<>();
        //needed for Testing
        destinationCards.add(new DestinationCard(cities.get(CityId.PHILOSOPHIE), cities.get(CityId.PHARMA), 5));
        destinationCards.add(new DestinationCard(cities.get(CityId.BIBLIOTHEK), cities.get(CityId.THEOLOGIE), 2));
        destinationCards.add(new DestinationCard(cities.get(CityId.WWZ), cities.get(CityId.GEOGRAPHIE), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.PHILOSOPHIE), cities.get(CityId.KOLLEGIENHAUS), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.PHARMA), cities.get(CityId.SPORT), 5));
        destinationCards.add(new DestinationCard(cities.get(CityId.BIOZENTRUM), cities.get(CityId.THEOLOGIE), 3));
        destinationCards.add(new DestinationCard(cities.get(CityId.DMI), cities.get(CityId.SBB), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.PHILOSOPHIE), cities.get(CityId.THEOLOGIE), 3));
        destinationCards.add(new DestinationCard(cities.get(CityId.BIOLOGIE), cities.get(CityId.KUNSTGESCHICHTE), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.SBB), cities.get(CityId.JUS), 3));
        destinationCards.add(new DestinationCard(cities.get(CityId.SBB), cities.get(CityId.GEOGRAPHIE), 5));
        destinationCards.add(new DestinationCard(cities.get(CityId.BIOZENTRUM), cities.get(CityId.BIOLOGIE), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.PHARMA), cities.get(CityId.DMI), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.KOLLEGIENHAUS), cities.get(CityId.POWI), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.SBB), cities.get(CityId.MEDIZIN), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.DMI), cities.get(CityId.DEUTSCH), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.BIBLIOTHEK), cities.get(CityId.CHEMIE), 4));
        destinationCards.add(new DestinationCard(cities.get(CityId.JUS), cities.get(CityId.GEOGRAPHIE), 2));
        destinationCards.add(new DestinationCard(cities.get(CityId.PHILOSOPHIE), cities.get(CityId.SBB), 5));
        destinationCards.add(new DestinationCard(cities.get(CityId.DMI), cities.get(CityId.PSYCHOLOGIE), 3));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    /**
     * Factory Method to create deck with only short destination cards
     * @param map must be the {@link GameMap#defaultMap()}
     */
    public static DestinationCardDeck longDestinations(GameMap map) {
        Map<String, City> cities = new HashMap<>();
        map.getCities().forEach((city -> cities.put(city.getId(), city)));

        List<DestinationCard> destinationCards = new ArrayList<>();
        //needed for Testing
        destinationCards.add(new DestinationCard(cities.get(CityId.DMI), cities.get(CityId.POWI), 6));
        destinationCards.add(new DestinationCard(cities.get(CityId.PHILOSOPHIE), cities.get(CityId.MEDIZIN), 7));
        destinationCards.add(new DestinationCard(cities.get(CityId.CHEMIE), cities.get(CityId.POWI), 6));
        destinationCards.add(new DestinationCard(cities.get(CityId.THEOLOGIE), cities.get(CityId.GEOGRAPHIE), 6));
        destinationCards.add(new DestinationCard(cities.get(CityId.PSYCHOLOGIE), cities.get(CityId.CHEMIE), 7));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    public List<DestinationCard> getCardDeck() {
        return cardDeck;
    }
}
