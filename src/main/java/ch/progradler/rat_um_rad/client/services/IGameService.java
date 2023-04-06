package ch.progradler.rat_um_rad.client.services;

import java.io.IOException;

/**
 * Allows client to send game-related requests to server.
 */
public interface IGameService {
    void createGame(int requiredPlayerCount) throws IOException;
}
