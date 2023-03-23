package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;

/**
 * Interface which allows calling service methods concerning a or multiple users
 */
public interface IUserService {
    void handleNewUser(String username, String ipAddress);

    void updateUsername(String username, String ipAddress);

    void handleUserDisconnected(String ipAddress);

    void handleMessageFromUser(ChatMessage message, String ipAddress);
}
