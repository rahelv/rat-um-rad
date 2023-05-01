package ch.progradler.rat_um_rad.server.services;

/**
 * Interface which allows calling service methods concerning a or multiple users
 */
public interface IUserService {
    void handleNewUser(String username, String ipAddress);

    void updateUsername(String username, String ipAddress);

    void handleUserDisconnected(String ipAddress);

    void requestOnlinePlayers(String ipAddress);

    void    handleBroadCastMessageFromUser(String message, String ipAddress);

    void handleGameInternalMessageFromUser(String message, String ipAddress);

    void handleWhisperMessageFromUser(String message, String toUsername, String ipAddress);
}
