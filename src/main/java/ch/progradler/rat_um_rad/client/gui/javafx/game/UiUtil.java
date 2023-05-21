package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import javafx.scene.paint.Color;

/**
 * Util class for UI - related things
 */
public class UiUtil {
    public static Color playerColor(PlayerColor color) {
        switch (color) {
            case LIGHT_BLUE -> {
                return Color.AQUAMARINE;
            }
            case LILA -> {
                return Color.CYAN;
            }
            case PINK -> {
                return Color.HOTPINK;
            }
            case LIGHT_BROWN -> {
                return Color.SANDYBROWN;
            }
            case LIGHT_GREEN -> {
                return Color.TURQUOISE;
            }
        }
        return null;
    }

    public static Color wheelCardColor(WheelColor color) {
        switch (color) {
            case RED -> {
                return Color.RED;
            }
            case BLUE -> {
                return Color.BLUE;
            }
            case ORANGE -> {
                return Color.ORANGE;
            }
            case GREEN -> {
                return Color.GREEN;
            }
            case YELLOW -> {
                return Color.YELLOW;
            }
            case WHITE -> {
                return Color.WHITE;
            }
            case BLACK -> {
                return Color.BLACK;
            }
        }
        return null;
    }
}
