package ch.progradler.rat_um_rad.shared.models.game.cards_and_decks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WheelCardTest {
    @Test
    void getColorIsCorrect() {
        assertColor(0, 9, WheelColor.RED);
        assertColor(10, 19, WheelColor.BLUE);
        assertColor(20, 29, WheelColor.ORANGE);
        assertColor(30, 39, WheelColor.GREEN);
        assertColor(40, 49, WheelColor.YELLOW);
        assertColor(50, 59, WheelColor.PINK);
        assertColor(60, 69, WheelColor.BLACK);
        assertColor(70, 79, WheelColor.WHITE);
        // assertColor(80, 89, WheelColor.JOKER); TODO: re-add
    }

    private void assertColor(int borderStart, int borderEnd, WheelColor color) {
        WheelCard atBorderStart = new WheelCard(borderStart);
        assertEquals(color, atBorderStart.getColor());
        WheelCard atBorderEnd = new WheelCard(borderEnd);
        assertEquals(color, atBorderEnd.getColor());
    }
}