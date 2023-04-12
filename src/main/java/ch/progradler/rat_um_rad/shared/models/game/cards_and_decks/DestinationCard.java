package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.models.game.City;

/**
 * The implementation of the "Lang/Kurz-Streckenkarten".
 */
public class DestinationCard {
    private final String cardID;
    private final City destination1;
    private final City destination2;
    private final int points;

    public DestinationCard(String cardID, City destination1, City destination2, int points) {
        this.cardID = cardID;
        this.destination1 = destination1;
        this.destination2 = destination2;
        this.points = points;
    }

    public String getCardID() {
        return cardID;
    }

    public City getDestination1() {
        return destination1;
    }

    public City getDestination2() {
        return destination2;
    }

    public int getPoints() {
        return points;
    }
}
