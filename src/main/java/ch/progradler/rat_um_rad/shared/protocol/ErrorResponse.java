package ch.progradler.rat_um_rad.shared.protocol;

/**
 * These are the error messages sent from server to client if a packet with command {@link Command#INVALID_ACTION_FATAL} is thrown.
 */
public class ErrorResponse {
    public static final String JOINING_NOT_POSSIBLE = "Joining game is not possible. Refresh game list.";
    public static final String USERNAME_INVALID = "Username invalid. Please try again";
}
