package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.util.GameConfig;

import java.util.ArrayList;
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
     * Uses same principle as determining if is players turn (total players count, playing order, turn)
     * @return the index of the color in {@link WheelColor}s
     */
    private int colorIndicator() {
        return (cardID % WheelColor.values().length);
    }

    public WheelColor getColor() {
        return WheelColor.values()[colorIndicator()];
    }

    /**
     * @return Mutable list of all wheel cards
     */
    public static List<WheelCard> all() {
        return new ArrayList<>(Stream.iterate(0, n -> n + 1)
                .limit(GameConfig.TOTAL_WHEEL_CARD_COUNT).map(WheelCard::new).toList());
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
