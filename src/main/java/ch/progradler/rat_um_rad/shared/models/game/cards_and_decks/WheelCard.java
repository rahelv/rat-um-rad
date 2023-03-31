package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implementation of the "Radkarten".
 */
public class WheelCard {

    private static final int TOTAL_WHEEL_CARD_COUNT = 90;

    private final int cardID;

    public WheelCard(int cardID) {
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }

    private int colorIndicator() {
        int roundedTo10s = cardID - (cardID % 10);
        return (roundedTo10s) / 10; // how many times a 10 was subtracted
    }

    public WheelColor getColor() {
        return WheelColor.values()[colorIndicator()];
    }

    public static List<WheelCard> all() {
        return Stream.iterate(1, n -> n + 1)
                .limit(TOTAL_WHEEL_CARD_COUNT).map(WheelCard::new).toList();
    }
}
