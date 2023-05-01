package ch.progradler.rat_um_rad.shared.util;

/**
 * Configuration values of the game.
 */
public class GameConfig {
    public static final int START_WHEEL_CARD_HANDOUT_COUNT = 6; // TODO: reduce to 4
    public static final int STARTING_WHEELS_PER_PLAYER = 15;
    public static final int TOTAL_WHEEL_CARD_COUNT = 240; // TODO: change to 90 when joker added
    public static final int SHORT_DEST_CARDS_AT_START_COUNT = 3;

    public static final int MAX_WHEELS_LEFT_TO_END_GAME = 5;

    public static int scoreForRoadBuild(int length) {
        // TODO: possibly add more cases
        return switch (length) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 7;
            case 5 -> 11;
            case 6 -> 15;
            default -> 0;
        };
    }
}
