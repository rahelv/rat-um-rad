package ch.progradler.rat_um_rad.shared.protocol;

/**
 * These are the error messages sent from server to client if a packet with command {@link Command#INVALID_ACTION_FATAL} is thrown.
 */
public class ErrorResponse {
    public static final String JOINING_NOT_POSSIBLE = "Joining game is not possible. Refresh game list.";
    public static final String USERNAME_INVALID = "Username invalid. Please try again";
    public static final String PLAYER_IN_NO_GAME = "You are not playing in any yet!";
    public static final String SELECTED_SHORT_DESTINATION_CARDS_INVALID = "Invalid selected short destination cards.";
}
