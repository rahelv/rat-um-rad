package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import ch.progradler.rat_um_rad.shared.util.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class WheelCardTest {
    @Test
    void getColorIsCorrect() {
        assertColor(WheelColor.RED);
        assertColor(WheelColor.BLUE);
        assertColor(WheelColor.ORANGE);
        assertColor(WheelColor.GREEN);
        assertColor(WheelColor.YELLOW);
        assertColor(WheelColor.PINK);
        assertColor(WheelColor.BLACK);
        assertColor(WheelColor.WHITE);
        // assertColor(WheelColor.JOKER); TODO: re-add
    }

    private void assertColor(WheelColor color) {
        // determine color in same principle as check if is player's turn (modulo)

        int colorsCount = WheelColor.values().length;

        for (int i = color.ordinal(); i < GameConfig.TOTAL_WHEEL_CARD_COUNT; i += colorsCount) {
            if (i != 0) {
                WheelCard below = new WheelCard(i - 1);
                assertNotEquals(color, below.getColor());
            }
            WheelCard cardForColor = new WheelCard(i);
            assertEquals(color, cardForColor.getColor());
            WheelCard above = new WheelCard(i + 1);
            assertNotEquals(color, above.getColor());
        }
    }
}