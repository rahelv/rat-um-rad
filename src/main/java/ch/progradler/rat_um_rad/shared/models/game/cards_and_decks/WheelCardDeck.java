package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the card deck of @link WheelCard.
 */
public class WheelCardDeck implements CardDeck {
    private final List<WheelCard> deckOfCards;

    private WheelCardDeck(List<WheelCard> deckOfCards) {
        this.deckOfCards = deckOfCards;
    }

    /**
     * Factory Method to create full deck of wheel cards.
     */
    public static WheelCardDeck full() {
        return new WheelCardDeck(WheelCard.all());
    }

    /**
     * Factory Method to create empty deck of wheel cards.
     */
    public static WheelCardDeck empty() {
        return new WheelCardDeck(new ArrayList<>());
    }

    public List<WheelCard> getDeckOfCards() {
        return deckOfCards;
    }
}
