package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
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
}
