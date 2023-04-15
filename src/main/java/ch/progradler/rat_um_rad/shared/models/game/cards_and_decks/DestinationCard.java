package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.models.game.City;

import java.util.Objects;

/**
 * The implementation of the "Lang/Kurz-Streckenkarten".
 */
public class DestinationCard {
    private final String cardID;
    private final int color;
    private final City destination1;
    private final City destination2;
    private final int points;

    public DestinationCard(String cardID, int color, City destination1, City destination2, int points) {
        this.cardID = cardID;
        this.color = color;
        this.destination1 = destination1;
        this.destination2 = destination2;
        this.points = points;
    }

    public String getCardID() {
        return cardID;
    }

    public int getColor() {
        return color;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationCard)) return false;
        DestinationCard that = (DestinationCard) o;
        return color == that.color && points == that.points && cardID.equals(that.cardID) && destination1.equals(that.destination1) && destination2.equals(that.destination2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardID, color, destination1, destination2, points);
    }
}
