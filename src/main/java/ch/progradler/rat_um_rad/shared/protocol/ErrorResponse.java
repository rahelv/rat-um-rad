package ch.progradler.rat_um_rad.shared.protocol;

/**
 * These are the error messages sent from server to client if a packet with command {@link ServerCommand#INVALID_ACTION_FATAL} is thrown.
 */
public class ErrorResponse {
    public static final String JOINING_NOT_POSSIBLE = "Joining game is not possible. Refresh game list.";
    public static final String USERNAME_INVALID = "Username invalid. Please try again";
    public static final String PLAYER_IN_NO_GAME = "You are not playing in any yet!";
    public static final String SELECTED_SHORT_DESTINATION_CARDS_INVALID = "Invalid selected short destination cards.";
    public static final String NOT_PLAYERS_TURN = "It is not your turn!";
    public static final String GAME_NOT_STARTED = "Game has not started yet or is already finished!";
    public static final String ROAD_ALREADY_BUILT_ON = "Invalid action. Road already built on!";
    public static final String ROAD_DOES_NOT_EXIST = "Road does not exist.";
    public static final String NOT_ENOUGH_CARDS_OF_REQUIRED_COLOR_TO_BUILD_ROAD = "Invalid action. You don't have enough cards of correct color to build on road!";
    public static final String NOT_ENOUGH_WHEELS_TO_BUILD_ROAD = "Invalid action. You don't have enough wheels to build on road!";
}
