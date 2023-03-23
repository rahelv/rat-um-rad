package ch.progradler.rat_um_rad.server.services;

/**
 * Interface which allows calling service methods concerning a or multiple users
 */
public interface IUserService {
    void handleNewUser(String username, String ipAddress);

    void updateUsername(String username, String ipAddress);

    void handleUserDisconnected(String ipAddress);

    void handleMessageFromUser(String message, String ipAddress);
}
