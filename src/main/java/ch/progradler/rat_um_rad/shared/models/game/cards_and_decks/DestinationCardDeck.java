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

    private DestinationCardDeck(List<DestinationCard> cardDeck) {
        this.cardDeck = cardDeck;
    }

    /**
     * Factory Method to create deck with only short destination cards
     */
    public static DestinationCardDeck shortDestinations() {
        List<DestinationCard> destinationCards = new ArrayList<>();
        //needed for Testing
        City city1 = new City("CityId1", "CityName1", new Point(1, 1));
        destinationCards.add(new DestinationCard("CardId1", 1, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 2, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 3, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 4, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 5, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 6, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 7, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 8, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 9, city1, city1, 1));
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
        destinationCards.add(new DestinationCard("CardId1", 1, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 2, city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId1", 3, city1, city1, 1));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    public List<DestinationCard> getCardDeck() {
        return cardDeck;
    }
}
