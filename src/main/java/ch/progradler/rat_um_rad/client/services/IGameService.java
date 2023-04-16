package ch.progradler.rat_um_rad.client.services;

import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;

import java.io.IOException;

/**
 * Allows client to send game-related requests to server.
 */
public interface IGameService {
    void createGame(int requiredPlayerCount) throws IOException;

    void buildRoad(String roadId) throws IOException;

    void buildGreyRoad(String roadId, WheelColor color) throws IOException;

    void requestWaitingGames() throws IOException;

    void requestStartedGames() throws IOException;

    void requestFinishedGames() throws IOException;
}
