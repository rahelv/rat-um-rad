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
        return new DestinationCardDeck(new ArrayList<>()); // TODO: implement correct cards
    }

    /**
     * Factory Method to create deck with only short destination cards
     */
    public static DestinationCardDeck longDestinations() {
        return new DestinationCardDeck(new ArrayList<>()); // TODO: implement correct cards
    }

    public List<DestinationCard> getCardDeck() {
        return cardDeck;
    }
}
