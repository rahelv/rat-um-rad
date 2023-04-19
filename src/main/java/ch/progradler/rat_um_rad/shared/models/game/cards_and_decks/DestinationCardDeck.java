package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.City;

import java.util.ArrayList;
import java.util.List;

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
     */
    public static DestinationCardDeck shortDestinations() {
        List<DestinationCard> destinationCards = new ArrayList<>();
        //needed for Testing
        City city1 = new City("CityId1", "Luzern", new Point(1, 1));
        City city2 = new City("CityId2", "Basel", new Point(1, 1));
        City city3 = new City("CityId3", "Bern", new Point(1, 1));
        City city4 = new City("CityId4", "Thun", new Point(1, 1));
        City city5 = new City("CityId5", "Biel", new Point(1, 1));
        City city6 = new City("CityId6", "Biozentrum", new Point(1, 1));
        City city7 = new City("CityId7", "Night City", new Point(1, 1));

        destinationCards.add(new DestinationCard("CardId1", city1, city2, 1));
        destinationCards.add(new DestinationCard("CardId2", city1, city3, 1));
        destinationCards.add(new DestinationCard("CardId3", city2, city6, 1));
        destinationCards.add(new DestinationCard("CardId4", city1, city7, 1));
        destinationCards.add(new DestinationCard("CardId5", city4, city2, 1));
        destinationCards.add(new DestinationCard("CardId6", city4, city1, 1));
        destinationCards.add(new DestinationCard("CardId7", city3, city5, 1));
        destinationCards.add(new DestinationCard("CardId8", city1, city3, 1));
        destinationCards.add(new DestinationCard("CardId9", city6, city7, 1));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    /**
     * Factory Method to create deck with only short destination cards
     */
    public static DestinationCardDeck longDestinations() {
        List<DestinationCard> destinationCards = new ArrayList<>();
        //needed for Testing
        City city1 = new City("CityId1", "CityName1", new Point(1, 1));
        destinationCards.add(new DestinationCard("longDestCard1", city1, city1, 5));
        destinationCards.add(new DestinationCard("longDestCard2", city1, city1, 4));
        destinationCards.add(new DestinationCard("longDestCard3", city1, city1, 6));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    public List<DestinationCard> getCardDeck() {
        return cardDeck;
    }
}
