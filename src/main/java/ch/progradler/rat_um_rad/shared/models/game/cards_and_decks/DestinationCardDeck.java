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
    public static DestinationCardDeck shortDestinations(GameMap map) {
        Map<String, City> cities = new HashMap<>();
        map.getCities().forEach((city -> cities.put(city.getId(), city)));

        List<DestinationCard> destinationCards = new ArrayList<>();
        //needed for Testing
        destinationCards.add(new DestinationCard("FromBaselToZuerich", cities.get(CityId.BASEL), cities.get(CityId.ZURICH), 5));
        destinationCards.add(new DestinationCard("FromZuerichToLuzern", cities.get(CityId.ZURICH), cities.get(CityId.LUZERN), 4));
        destinationCards.add(new DestinationCard("FromLuzernToBern", cities.get(CityId.LUZERN), cities.get(CityId.BERN), 6));
        destinationCards.add(new DestinationCard("FromBernToBasel", cities.get(CityId.BERN), cities.get(CityId.BASEL), 4));
        destinationCards.add(new DestinationCard("FromBaselToLuzern", cities.get(CityId.BASEL), cities.get(CityId.LUZERN), 5 ));
        destinationCards.add(new DestinationCard("FromBernToGenf", cities.get(CityId.BERN), cities.get(CityId.GENF), 3));
        destinationCards.add(new DestinationCard("FromGenfToNeuchatel", cities.get(CityId.GENF), cities.get(CityId.NEUCHATEL), 3));
        destinationCards.add(new DestinationCard("FromNeuchatelToBasel", cities.get(CityId.NEUCHATEL), cities.get(CityId.BASEL), 3));
        destinationCards.add(new DestinationCard("FromNeuchatelToBern", cities.get(CityId.NEUCHATEL), cities.get(CityId.BERN), 3));
        destinationCards.add(new DestinationCard("FromZuerichToChur", cities.get(CityId.ZURICH), cities.get(CityId.CHUR), 3));
        destinationCards.add(new DestinationCard("FromChurToLugano", cities.get(CityId.CHUR), cities.get(CityId.LUGANO), 3));
        destinationCards.add(new DestinationCard("FromLuganoToLuzern", cities.get(CityId.LUGANO), cities.get(CityId.LUZERN), 3));
        destinationCards.add(new DestinationCard("FromZuerichToLugano", cities.get(CityId.ZURICH), cities.get(CityId.LUGANO), 3));
        destinationCards.add(new DestinationCard("FromChurToNeuchatel", cities.get(CityId.CHUR), cities.get(CityId.NEUCHATEL), 3));
        destinationCards.add(new DestinationCard("FromChurToBasel", cities.get(CityId.CHUR), cities.get(CityId.BASEL), 3));
        destinationCards.add(new DestinationCard("FromGenfToLuzern", cities.get(CityId.GENF), cities.get(CityId.LUZERN), 3));
        destinationCards.add(new DestinationCard("FromLuganoToGenf", cities.get(CityId.LUGANO), cities.get(CityId.GENF), 3));

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
        destinationCards.add(new DestinationCard("FromBaselToZuerich", cities.get(CityId.BASEL), cities.get(CityId.ZURICH), 5));
        destinationCards.add(new DestinationCard("FromZuerichToLuzern", cities.get(CityId.ZURICH), cities.get(CityId.LUZERN), 4));
        destinationCards.add(new DestinationCard("FromLuzernToBern", cities.get(CityId.LUZERN), cities.get(CityId.BERN), 6));
        destinationCards.add(new DestinationCard("FromBernToBasel", cities.get(CityId.BERN), cities.get(CityId.BASEL), 4));
        destinationCards.add(new DestinationCard("FromBaselToLuzern", cities.get(CityId.BASEL), cities.get(CityId.LUZERN), 5 ));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    public List<DestinationCard> getCardDeck() {
        return cardDeck;
    }
}
