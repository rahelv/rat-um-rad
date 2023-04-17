package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.util.GameConfig;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Implementation of the "Radkarten".
 */
public class WheelCard {

    private final int cardID;

    public WheelCard(int cardID) {
        this.cardID = cardID;
    }

    public int getCardID() {
        return cardID;
    }

    /**
     * There are 8 colors (see {@link WheelColor}s).
     * The color of a card is determined by the id,
     * such that the index of the color is the rest of {@link WheelCard#cardID} divided by 10.
     *
     * @return the index of the color in {@link WheelColor}s
     */
    private int colorIndicator() {
        int roundedTo10s = cardID - (cardID % 10);
        return (roundedTo10s) / 10; // how many times a 10 was subtracted
    }

    public WheelColor getColor() {
        return WheelColor.values()[colorIndicator()];
    }

    public static List<WheelCard> all() {
        return Stream.iterate(0, n -> n + 1)
                .limit(GameConfig.TOTAL_WHEEL_CARD_COUNT).map(WheelCard::new).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WheelCard wheelCard = (WheelCard) o;
        return cardID == wheelCard.cardID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardID);
    }
}
