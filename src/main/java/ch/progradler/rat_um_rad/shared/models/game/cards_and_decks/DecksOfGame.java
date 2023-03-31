package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

/**
 * This is a collection of all card decks in the game that don't belong to a player.
 */
public class DecksOfGame {
    private WheelCardDeck wheelCardDeck;
    private WheelCardDeck discardDeck;
    private DestinationCardDeck longDestinationCardDeck;
    private DestinationCardDeck shortDestinationCardDeck;

    public DecksOfGame(WheelCardDeck wheelCardDeck, WheelCardDeck discardDeck, DestinationCardDeck longDestinationCardDeck, DestinationCardDeck shortDestinationCardDeck) {
        this.wheelCardDeck = wheelCardDeck;
        this.discardDeck = discardDeck;
        this.longDestinationCardDeck = longDestinationCardDeck;
        this.shortDestinationCardDeck = shortDestinationCardDeck;
    }

    public static DecksOfGame startingDecks() {
        return new DecksOfGame(
                WheelCardDeck.full(),
                WheelCardDeck.empty(),
                DestinationCardDeck.longDestinations(),
                DestinationCardDeck.shortDestinations()
        );
    }

    public WheelCardDeck getWheelCardDeck() {
        return wheelCardDeck;
    }

    public WheelCardDeck getDiscardDeck() {
        return discardDeck;
    }

    public DestinationCardDeck getLongDestinationCardDeck() {
        return longDestinationCardDeck;
    }

    public DestinationCardDeck getShortDestinationCardDeck() {
        return shortDestinationCardDeck;
    }

    public void setWheelCardDeck(WheelCardDeck wheelCardDeck) {
        this.wheelCardDeck = wheelCardDeck;
    }

    public void setDiscardDeck(WheelCardDeck discardDeck) {
        this.discardDeck = discardDeck;
    }

    public void setLongDestinationCardDeck(DestinationCardDeck longDestinationCardDeck) {
        this.longDestinationCardDeck = longDestinationCardDeck;
    }

    public void setShortDestinationCardDeck(DestinationCardDeck shortDestinationCardDeck) {
        this.shortDestinationCardDeck = shortDestinationCardDeck;
    }
}
