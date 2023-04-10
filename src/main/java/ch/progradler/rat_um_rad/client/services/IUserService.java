package ch.progradler.rat_um_rad.client.services;

import java.io.IOException;

/**
 * Allows client to send user-related requests to server.
 */
public interface IUserService {
    void sendUsername(String username) throws IOException;

    void changeUsername(String username) throws IOException;

    void sendBroadCastMessage(String message) throws IOException;

    void sendWhisperMessage(String message, String toUsername) throws IOException;
    void requestOnlinePlayers() throws IOException;
}
