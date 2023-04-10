package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.List;

/**
 * Interface which allows calling service methods concerning a or multiple users
 */
public interface IUserService {
    void handleNewUser(String username, String ipAddress);

    void updateUsername(String username, String ipAddress);

    void handleUserDisconnected(String ipAddress);

    void requestOnlinePlayers(String ipAddress);

    void handleBroadCastMessageFromUser(String message, String ipAddress);

    void handleWhisperMessageFromUser(String message, String toUsername, String ipAddress);
}
