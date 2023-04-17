package ch.progradler.rat_um_rad.shared.util;

/**
 * Configuration values of the game.
 */
public class GameConfig {
    public static final int STARTING_WHEELS_PER_PLAYER = 35;
    public static final int TOTAL_WHEEL_CARD_COUNT = 80; // TODO: change to 90 when joker added

    public static int scoreForRoadBuild(int length) {
        // TODO: possibly add more cases
        return switch (length) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 7;
            default -> 0;
        };
    }
}
