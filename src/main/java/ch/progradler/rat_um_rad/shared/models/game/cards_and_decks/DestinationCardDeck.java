package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

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
        destinationCards.add(new DestinationCard("CardId1",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId2",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId3",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId4",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId5",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId6",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId7",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId8",  city1, city1, 1));
        destinationCards.add(new DestinationCard("CardId9",  city1, city1, 1));
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
        destinationCards.add(new DestinationCard("longDestCard1",  city1, city1, 5));
        destinationCards.add(new DestinationCard("longDestCard2",  city1, city1, 4));
        destinationCards.add(new DestinationCard("longDestCard3",  city1, city1, 6));
        return new DestinationCardDeck(destinationCards);

        // TODO: implement correct cards
    }

    public List<DestinationCard> getCardDeck() {
        return cardDeck;
    }
}
